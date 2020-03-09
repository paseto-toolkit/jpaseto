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
import org.testng.annotations.Test

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class Blake2bTest {

    @Test
    void blake2bTest() {
        // from https://github.com/BLAKE2/BLAKE2/blob/master/testvectors/blake2b-kat.txt
        def payload = decode("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f20")
        def key = decode("000102030405060708090a0b0c0d0e0f101112131415161718191a1b1c1d1e1f202122232425262728292a2b2c2d2e2f303132333435363738393a3b3c3d3e3f")
        def expectedHash = decode("5595e05c13a7ec4dc8f41fb70cb50a71bce17c024ff6de7af618d0cc4e9c32d9570d6d3ea45b86525491030c0d8f2b1836d5778c1ce735c17707df364d054347")

        def result = Blake2b.hash(expectedHash.length, payload, key)
        assertThat result, is(expectedHash)
    }

    private static byte[] decode(String input) {
        return Hex.decodeHex(input)
    }
}
