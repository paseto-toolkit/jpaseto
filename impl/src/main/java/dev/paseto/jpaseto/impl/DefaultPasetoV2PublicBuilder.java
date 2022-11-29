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

import dev.paseto.jpaseto.PasetoTokenBuilder;
import dev.paseto.jpaseto.PasetoV2LocalBuilder;
import dev.paseto.jpaseto.PasetoV2PublicBuilder;
import dev.paseto.jpaseto.impl.crypto.V2PublicCryptoProvider;

import java.security.PrivateKey;

@AutoService(PasetoV2PublicBuilder.class)
public class DefaultPasetoV2PublicBuilder extends AbstractPasetoBuilder<PasetoV2PublicBuilder> implements PasetoV2PublicBuilder {

    private static final String HEADER = "v2.public.";

    private PrivateKey privateKey;

    private final V2PublicCryptoProvider cryptoProvider;

    public DefaultPasetoV2PublicBuilder() {
        this(CryptoProviders.v2PublicCryptoProvider());
    }

    private DefaultPasetoV2PublicBuilder(V2PublicCryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    @Override
    public PasetoV2PublicBuilder setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public String compact(PasetoTokenBuilder t) {
        t.setSerializer(this.getSerializer());

        byte[] payload = t.payloadAsBytes();
        byte[] footer = t.footerAsBytes();

        byte[] signature = cryptoProvider.sign(payload, footer, privateKey);

        return HEADER + t.noPadBase64(payload, signature) + t.footerToString(footer);
    }
}
