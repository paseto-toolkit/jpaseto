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

import dev.paseto.jpaseto.PasetoKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface V1LocalCryptoProvider {

    byte[] encrypt(byte[] payload, byte[] footer, byte[] nonce, SecretKey sharedSecret);

    byte[] decrypt(byte[] encryptedBytes, byte[] footer, byte[] nonce, SecretKey sharedSecret);

    default byte[] nonce(byte[] payload, byte[] randomBytes) {
        return Hmacs.createNonce(randomBytes, payload);
    }

    static byte[] doCipher(int mode, byte[] key, byte[] nonce, byte[] input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(mode, secretKeySpec, new IvParameterSpec(nonce));
            return cipher.doFinal(input);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new SecurityException("Failed create cipher.");
        }
    }
}
