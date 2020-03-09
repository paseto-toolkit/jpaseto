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
package dev.paseto.jpaseto.crypto.sodium;

import com.google.auto.service.AutoService;
import com.google.common.primitives.Bytes;
import dev.paseto.jpaseto.PasetoSecurityException;
import dev.paseto.jpaseto.impl.crypto.PreAuthEncoder;
import dev.paseto.jpaseto.impl.crypto.V2LocalCryptoProvider;
import org.apache.tuweni.crypto.sodium.XChaCha20Poly1305;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

@AutoService(V2LocalCryptoProvider.class)
public class SodiumV2LocalCryptoProvider implements V2LocalCryptoProvider {

    private static final byte[] HEADER_BYTES = "v2.local.".getBytes(StandardCharsets.UTF_8);

    @Override
    public byte[] blake2b(byte[] payload, byte[] random) {
        return Blake2b.hash(24, payload, random);
    }

    @Override
    public byte[] encrypt(byte[] payload, byte[] footer, byte[] nonce, SecretKey sharedSecret) {
        // 4
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, nonce, footer);

        // 5
        byte[] payloadCipher = XChaCha20Poly1305.encrypt(payload,
                preAuth,
                XChaCha20Poly1305.Key.fromBytes(sharedSecret.getEncoded()),
                XChaCha20Poly1305.Nonce.fromBytes(nonce));

        // 6
        return Bytes.concat(nonce, payloadCipher);
    }

    @Override
    public byte[] decrypt(byte[] encryptedBytes, byte[] footer, SecretKey sharedSecret) {
        byte[] nonce = Arrays.copyOf(encryptedBytes, 24); // nonce size is 24 bytes
        byte[] encryptedMessage = Arrays.copyOfRange(encryptedBytes, 24, encryptedBytes.length);

        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, nonce, footer);

        byte[] payloadBytes = XChaCha20Poly1305.decrypt(
                encryptedMessage,
                preAuth,
                XChaCha20Poly1305.Key.fromBytes(sharedSecret.getEncoded()),
                XChaCha20Poly1305.Nonce.fromBytes(nonce));

        if (payloadBytes == null) {
            throw new PasetoSecurityException("Decryption failed, likely cause is an invalid sharedSecret or MAC.");
        }

        return payloadBytes;
    }
}