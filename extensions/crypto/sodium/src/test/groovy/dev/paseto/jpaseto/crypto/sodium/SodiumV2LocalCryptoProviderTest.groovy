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
package dev.paseto.jpaseto.crypto.sodium

import org.apache.commons.codec.binary.Hex
import org.hamcrest.MatcherAssert
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class SodiumV2LocalCryptoProviderTest {

    @Test
    void blake2bTest() {
        // test vector with a 24 hash length
        def payload = decode("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20")
        def key = decode("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f")
        def expectedHash = decode("46cbab86d3dd2e34f925259a0cf69cc92cf48f094cecd403")

        def result = new SodiumV2LocalCryptoProvider().blake2b(payload, key)
        assertThat result, is(expectedHash)
    }

    private static byte[] decode(String input) {
        return Hex.decodeHex(input)
    }
}
