/*
 * Copyright 2021-Present paseto.dev
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
package dev.paseto.jpaseto.crypto.bouncycastle

import org.bouncycastle.jce.provider.BouncyCastleProvider
import org.testng.annotations.Test

import java.security.Provider
import java.security.Security

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*

class BouncyCastleInitializerTest {

    @Test
    void testEnableBouncyCastle() {

        // Call enable
        BouncyCastleInitializer.enableBouncyCastle()
        // make sure BC has been registered
        assertThat findAllBCProviders(), allOf(
                hasSize(1),
                contains(instanceOf(BouncyCastleProvider))
        )
        Provider provider = Security.getProvider("BC")

        // Call enable again, this should NOT register a new instance
        BouncyCastleInitializer.enableBouncyCastle()

        // check for single item, with the same provider instance
        assertThat findAllBCProviders(), allOf(
                hasSize(1),
                contains(sameInstance(provider))
        )
    }

    private static Collection<Provider> findAllBCProviders() {
        return Security.getProviders().findAll {it.getName().equals("BC") }
    }
}
