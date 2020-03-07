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
package dev.paseto.jpaseto.impl;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.PasetoV2LocalBuilder;
import dev.paseto.jpaseto.impl.crypto.V2LocalCryptoProvider;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


@AutoService(PasetoV2LocalBuilder.class)
public class DefaultPasetoV2LocalBuilder extends AbstractPasetoBuilder<PasetoV2LocalBuilder> implements PasetoV2LocalBuilder {

    private static final String HEADER = "v2.local.";

    private SecretKey sharedSecret = null;

    private final V2LocalCryptoProvider cryptoProvider;

    public DefaultPasetoV2LocalBuilder() {
        this(CryptoProviders.v2LocalCryptoProvider());
    }

    DefaultPasetoV2LocalBuilder(V2LocalCryptoProvider cryptoProvider) {
     this.cryptoProvider = cryptoProvider;
    }

    @Override
    public PasetoV2LocalBuilder setSharedSecret(SecretKey sharedSecret) {
        this.sharedSecret = sharedSecret;
        return this;
    }

    @Override
    public String compact() {

        byte[] payload = payloadAsBytes();
        byte[] footer = footerAsBytes();

        // 2
        byte[] randomBytes = new byte[24];
        try {
            SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("JVM does not provide a strong secure random number generator", e);
        }

        // 3
        byte[] nonce = cryptoProvider.blake2b(randomBytes, payload);

        // 4, 5, 6
        byte[] cipherText = cryptoProvider.encrypt(payload, footer, nonce, sharedSecret);

        String base64d = noPadBase64(cipherText);

        return HEADER + base64d + footerToString(footer);
    }
}
