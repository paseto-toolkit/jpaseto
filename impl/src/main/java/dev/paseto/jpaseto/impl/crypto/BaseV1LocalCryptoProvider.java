/*
 * Copyright 2019-Present paseto.dev
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
package dev.paseto.jpaseto.impl.crypto;

import dev.paseto.jpaseto.InvalidMacException;
import dev.paseto.jpaseto.impl.lang.Bytes;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.security.MessageDigest;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

public abstract class BaseV1LocalCryptoProvider implements V1LocalCryptoProvider {

    private static final byte[] HEADER_BYTES = "v1.local.".getBytes(UTF_8);

    @Override
    public byte[] encrypt(byte[] payload, byte[] footer, byte[] nonce, SecretKey sharedSecret) {

        byte[] salt = Arrays.copyOf(nonce, 16);
        byte[] rightNonce = Arrays.copyOfRange(nonce, 16, nonce.length);

        // 4
        byte[] encryptionKey = encryptionKey(sharedSecret, salt);
        byte[] authenticationKey = authenticationKey(sharedSecret, salt);

        // 5
        byte[] cipherText = V1LocalCryptoProvider.doCipher(Cipher.ENCRYPT_MODE, encryptionKey, rightNonce, payload);

        //6
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, nonce, cipherText, footer);

        //7
        byte[] calculatedMac = Hmacs.hmacSha384(authenticationKey, preAuth);

        // 8
        return Bytes.concat(nonce, cipherText, calculatedMac);
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes, byte[] footer, byte[] nonce, SecretKey sharedSecret) {

        // 3
        byte[] salt = Arrays.copyOf(nonce, 16);
        byte[] rightNonce = Arrays.copyOfRange(nonce, 16, nonce.length);

        byte[] cipherText = Arrays.copyOfRange(encryptedBytes, 32, encryptedBytes.length - 48);
        byte[] mac = Arrays.copyOfRange(encryptedBytes, encryptedBytes.length - 48, encryptedBytes.length);

        // 4
        byte[] encryptionKey = encryptionKey(sharedSecret, salt);
        byte[] authenticationKey = authenticationKey(sharedSecret, salt);

        // 5
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, nonce, cipherText, footer);

        // 6
        byte[] calculatedMac = Hmacs.hmacSha384(authenticationKey, preAuth);

        // 7
        if (!MessageDigest.isEqual(calculatedMac, mac)) {
            throw new InvalidMacException("Failed to validate mac in token");
        }

        // 8
        return V1LocalCryptoProvider.doCipher(Cipher.DECRYPT_MODE, encryptionKey, rightNonce, cipherText);
    }

    private byte[] encryptionKey(SecretKey sharedSecret, byte[] salt) {
        return hkdfSha384(sharedSecret, salt, "paseto-encryption-key");
    }

    private byte[] authenticationKey(SecretKey sharedSecret, byte[] salt) {
        return hkdfSha384(sharedSecret, salt, "paseto-auth-key-for-aead");
    }

    private byte[] hkdfSha384(SecretKey sharedSecret, byte[] salt, String info) {
        return hkdfSha384(sharedSecret, salt, info.getBytes(UTF_8));
    }

    protected abstract byte[] hkdfSha384(SecretKey sharedSecret, byte[] salt, byte[] info);
}
