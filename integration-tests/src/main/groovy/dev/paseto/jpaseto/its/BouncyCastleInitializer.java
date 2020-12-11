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
package dev.paseto.jpaseto.its;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;

final class BouncyCastleInitializer {

    private static final AtomicBoolean bcLoaded = new AtomicBoolean(false);

    private BouncyCastleInitializer() {}

    static void enableBouncyCastle() {
        for(Provider provider : Security.getProviders()) {
            if (BouncyCastleProvider.PROVIDER_NAME.equals(provider.getName())) {
                bcLoaded.set(true);
                return;
            }
        }

        //bc provider not enabled - add it:
        Security.addProvider(new BouncyCastleProvider());
        bcLoaded.set(true);
    }
}
