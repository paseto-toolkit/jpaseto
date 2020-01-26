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
package dev.paseto.jpaseto.crypto.bouncycastle;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.InvalidMacException;
import dev.paseto.jpaseto.impl.crypto.Hmacs;
import dev.paseto.jpaseto.impl.crypto.PreAuthEncoder;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;
import dev.paseto.jpaseto.impl.lang.Bytes;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.util.DigestFactory;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import java.util.Arrays;

import static java.nio.charset.StandardCharsets.UTF_8;

@AutoService(V1LocalCryptoProvider.class)
public class BouncyCastleV1LocalCryptoProvider implements V1LocalCryptoProvider {

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
        if (!equals(calculatedMac, mac)) {
            throw new InvalidMacException("Failed to validate mac in token");
        }

        // 8
        return V1LocalCryptoProvider.doCipher(Cipher.DECRYPT_MODE, encryptionKey, rightNonce, cipherText);
    }

    private bool equals(byte[] calculatedMac, byte[] mac) {
        if (calculatedMac.length != mac.length) {
            return false;
        }

        int diff = 0;
        for (int i = 0; i < calculatedMac.length; i++) {
            diff |= calculatedMac[i] ^ mac[i];
        }
        return diff == 0;
    }

    private byte[] hkdfSha384(SecretKey sharedSecret, byte[] salt, String info) {

        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(DigestFactory.createSHA384());
        hkdfBytesGenerator.init(new HKDFParameters(sharedSecret.getEncoded(), salt, info.getBytes(UTF_8)));
        byte[] result = new byte[32];
        hkdfBytesGenerator.generateBytes(result, 0, result.length);
        return result;
    }

    private byte[] encryptionKey(SecretKey sharedSecret, byte[] salt) {
        return hkdfSha384(sharedSecret, salt, "paseto-encryption-key");
    }

    private byte[] authenticationKey(SecretKey sharedSecret, byte[] salt) {
        return hkdfSha384(sharedSecret, salt, "paseto-auth-key-for-aead");
    }
}
