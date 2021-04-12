/*
 * Copyright 2020-Present paseto.dev
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

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.PasetoSecurityException;
import dev.paseto.jpaseto.impl.crypto.PreAuthEncoder;
import dev.paseto.jpaseto.impl.crypto.V2LocalCryptoProvider;
import dev.paseto.jpaseto.impl.lang.Bytes;
import org.bouncycastle.crypto.digests.Blake2bDigest;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@AutoService(V2LocalCryptoProvider.class)
public class BouncyCastleV2LocalCryptoProvider implements V2LocalCryptoProvider {

    private static final byte[] HEADER_BYTES = "v2.local.".getBytes(StandardCharsets.UTF_8);

    @Override
    public byte[] blake2b(byte[] payload, byte[] random) {
        byte[] hash = new byte[24];
        Blake2bDigest digest = new Blake2bDigest(random, 24, null, null);
        digest.update(payload, 0, payload.length);
        digest.doFinal(hash, 0);
        return hash;
    }

    @Override
    public byte[] encrypt(byte[] payload, byte[] footer, byte[] nonce, SecretKey sharedSecret) {
        // 4
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, nonce, footer);

        byte[] payloadCipher;
        try {
            // 5
            Cipher cipher = XChaCha20Poly1305.cryptWith(true, sharedSecret.getEncoded(), nonce, preAuth);
            payloadCipher = cipher.doFinal(payload);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new PasetoSecurityException("Failed to encrypt token", e);
        }

        // 6
        return Bytes.concat(nonce, payloadCipher);
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes, byte[] footer, SecretKey sharedSecret) {
        byte[] nonce = Arrays.copyOf(encryptedBytes, 24); // nonce size is 24 bytes
        byte[] encryptedMessage = Arrays.copyOfRange(encryptedBytes, 24, encryptedBytes.length);
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, nonce, footer);

        byte[] payloadBytes;
        try {
            // 5
            Cipher cipher = XChaCha20Poly1305.cryptWith(false, sharedSecret.getEncoded(), nonce, preAuth);
            payloadBytes = cipher.doFinal(encryptedMessage);
        } catch (IllegalBlockSizeException | BadPaddingException e) {
            throw new PasetoSecurityException("Decryption failed, likely cause is an invalid sharedSecret or MAC.", e);
        }

        return payloadBytes;
    }
}
