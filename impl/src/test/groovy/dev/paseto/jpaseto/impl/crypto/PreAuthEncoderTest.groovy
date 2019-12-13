/*
 * Copyright 2019-Present paseto.dev, Inc.
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
package dev.paseto.jpaseto.impl.crypto

import org.apache.commons.codec.binary.Hex
import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.nio.charset.StandardCharsets

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class PreAuthEncoderTest {

    @Test(dataProvider="standardTestData")
    void standardTests(byte[][] listOfBytes, String expected) {
        assertThat(PreAuthEncoder.encode(listOfBytes), is(Hex.decodeHex(expected)))
    }

    @DataProvider
    Object[][] standardTestData() {
        return [
                [arrays(), "0000000000000000"],
                [arrays(""), "01000000000000000000000000000000"],
                [arrays("", ""), "020000000000000000000000000000000000000000000000"],
                [arrays("Paragon"), "0100000000000000070000000000000050617261676f6e"],
                [arrays("Paragon", "Initiative"), "0200000000000000070000000000000050617261676f6e0a00000000000000496e6974696174697665"],
                [arrays("Paragon\n\u0000\u0000\u0000\u0000\u0000\u0000\u0000Initiative"), "0100000000000000190000000000000050617261676f6e0a00000000000000496e6974696174697665"]
        ]
    }

    static byte[][] arrays(String... items = new String[0][0]) {
        byte[][] bytes = new byte[items.length]
        Arrays.stream(items).eachWithIndex { String entry, int ii -> bytes[ii] = entry.getBytes(StandardCharsets.UTF_8)}
        return bytes
    }
}
