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

import dev.paseto.jpaseto.impl.crypto.JcaV1PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.JcaV2PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V2LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V2PublicCryptoProvider;
import dev.paseto.jpaseto.lang.Services;

class CryptoProviders {

    private CryptoProviders() {}

    static V1LocalCryptoProvider v1LocalCryptoProvider() {
        return  Services.loadFirst(V1LocalCryptoProvider.class);
    }

    static V1PublicCryptoProvider v1PublicCryptoProvider() {
        return  Services.loadFirst(V1PublicCryptoProvider.class, new JcaV1PublicCryptoProvider());
    }

    static V2LocalCryptoProvider v2LocalCryptoProvider() {
        return  Services.loadFirst(V2LocalCryptoProvider.class);
    }

    static V2PublicCryptoProvider v2PublicCryptoProvider() {
        return  Services.loadFirst(V2PublicCryptoProvider.class, new JcaV2PublicCryptoProvider());
    }
}
