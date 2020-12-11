/*
 * Copyright 2020 Matt Sicker
 * Modifications Copyright 2020-Present paseto.dev
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

import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.bouncycastle.util.encoders.Hex.decode

class XChaCha20Poly1305Test {

    @Test
    void subkeyDerivation() {
        def key = decode("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f")
        def nonce = decode("000000090000004a0000000031415927")
        def expectedKey = decode("82413b4227b27bfed30e42508a877d73a0f9e4d58a74a853c12ec41326d3ecdc")
        def actualKey = XChaCha20Poly1305.calculateSubKey(key, nonce)
        assertThat expectedKey, equalTo(actualKey)
    }
}