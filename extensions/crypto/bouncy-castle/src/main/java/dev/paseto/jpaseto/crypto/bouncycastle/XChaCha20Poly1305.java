/*
 * Copyright 2020 Matt Sicker
 * Modifications Copyright 2020-Present paseto.dev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package dev.paseto.jpaseto.crypto.bouncycastle;

import org.bouncycastle.util.Arrays;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

/**
 * A Java implementation of XChaCha20Poly1305 using Bouncy Castle.
 * Adapted from: https://github.com/o1c-dev/o1c/blob/567a420aacb4c41b415e0413a4b347ec416bb7e9/java8/src/main/java/dev/o1c/spi/XChaCha20Poly1305.java
 * @see <a href="https://tools.ietf.org/html/rfc8439">rfc8439</a>
 * @see <a href="https://tools.ietf.org/html/draft-irtf-cfrg-xchacha-03">draft rfc xchacha</a>
 */
final class XChaCha20Poly1305 {
    private static final int KEY_SIZE = 32;
    private static final int[] ENGINE_STATE_HEADER = unpackIntsLE("expand 32-byte k".getBytes(StandardCharsets.US_ASCII), 0, 4);

    private XChaCha20Poly1305() {}

    static Cipher cryptWith(boolean forEncryption, byte[] key, byte[] nonce, byte[] aad) {
        Cipher cipher = getChaCha20Poly1305();
        byte[] hNonce = Arrays.copyOfRange(nonce, 0, 16);
        byte[] sNonce = Arrays.copyOfRange(nonce, 12, nonce.length);
        packIntLE(0, sNonce, 0);
        SecretKey subkey = new SecretKeySpec(calculateSubKey(key, hNonce), "XChaCha20-Poly1305");
        IvParameterSpec iv = new IvParameterSpec(sNonce);
        try {
            cipher.init(forEncryption ? Cipher.ENCRYPT_MODE : Cipher.DECRYPT_MODE, subkey, iv);
            cipher.updateAAD(aad);
        } catch (InvalidKeyException | InvalidAlgorithmParameterException e) {
            throw new IllegalStateException(e);
        }
        return cipher;
    }

    static byte[] calculateSubKey(byte[] key, byte[] nonce) {
        int[] state = Arrays.copyOf(ENGINE_STATE_HEADER, 16);
        unpackIntsLE(key, 0, 8, state, 4);
        unpackIntsLE(nonce, 0, 4, state, 12);
        chaChaBlock(state);
        byte[] subkey = new byte[KEY_SIZE];
        packIntsLE(state, 0, 4, subkey, 0);
        packIntsLE(state, 12, 4, subkey, 16);
        return subkey;
    }

    private static void chaChaBlock(int[] state) {
        for (int i = 0; i < 10; i++) {
            columnRound(state);
            diagonalRound(state);
        }
    }

    private static void columnRound(int[] state) {
        quarterRound(state, 0, 4, 8, 12);
        quarterRound(state, 1, 5, 9, 13);
        quarterRound(state, 2, 6, 10, 14);
        quarterRound(state, 3, 7, 11, 15);
    }

    private static void diagonalRound(int[] state) {
        quarterRound(state, 0, 5, 10, 15);
        quarterRound(state, 1, 6, 11, 12);
        quarterRound(state, 2, 7, 8, 13);
        quarterRound(state, 3, 4, 9, 14);
    }

    private static void quarterRound(int[] state, int a, int b, int c, int d) {
        state[a] += state[b];
        state[d] = shiftLeftRotate(state[d] ^ state[a], 16);

        state[c] += state[d];
        state[b] = shiftLeftRotate(state[b] ^ state[c], 12);

        state[a] += state[b];
        state[d] = shiftLeftRotate(state[d] ^ state[a], 8);

        state[c] += state[d];
        state[b] = shiftLeftRotate(state[b] ^ state[c], 7);
    }

    // val <<< len
    private static int shiftLeftRotate(int val, int len) {
        return (val << len) | (val >>> -len);
    }

    private static Cipher getChaCha20Poly1305() {
        try {
            return Cipher.getInstance("ChaCha20-Poly1305");
        } catch (NoSuchAlgorithmException | NoSuchPaddingException e) {
            throw new IllegalStateException(e);
        }
    }

    private static void packIntLE(int value, byte[] dst, int off) {
        dst[off] = (byte) value;
        dst[off + 1] = (byte) (value >>> 8);
        dst[off + 2] = (byte) (value >>> 16);
        dst[off + 3] = (byte) (value >>> 24);
    }

    private static void packIntsLE(int[] values, int off, int nrInts, byte[] dst, int dstOff) {
        for (int i = 0; i < nrInts; i++) {
            packIntLE(values[off + i], dst, dstOff + i * 4);
        }
    }

    private static int unpackIntLE(byte[] buf, int off) {
        return buf[off] & 0xff | (buf[off + 1] & 0xff) << 8 | (buf[off + 2] & 0xff) << 16 | (buf[off + 3] & 0xff) << 24;
    }

    private static void unpackIntsLE(byte[] buf, int off, int nrInts, int[] dst, int dstOff) {
        for (int i = 0; i < nrInts; i++) {
            dst[dstOff + i] = unpackIntLE(buf, off + i * 4);
        }
    }

    private static int[] unpackIntsLE(byte[] buf, int off, int nrInts) {
        int[] values = new int[nrInts];
        unpackIntsLE(buf, off, nrInts, values, 0);
        return values;
    }
}