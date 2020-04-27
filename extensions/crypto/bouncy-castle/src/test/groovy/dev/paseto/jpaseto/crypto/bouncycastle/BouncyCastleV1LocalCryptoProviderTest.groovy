/*
 * Copyright 2020-Present paseto.dev
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

import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider
import dev.paseto.jpaseto.lang.Keys
import dev.paseto.jpaseto.lang.Services
import org.testng.annotations.Test

import javax.crypto.SecretKey
import java.nio.charset.StandardCharsets

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.equalTo
import static org.hamcrest.Matchers.instanceOf
class BouncyCastleV1LocalCryptoProviderTest {

    @Test
    void loadServiceTest() {
        assertThat Services.loadFirst(V1LocalCryptoProvider), instanceOf(BouncyCastleV1LocalCryptoProvider)
    }

    @Test
    void hkdfSha384Test() {
        SecretKey secretKey = Keys.secretKey(decode("3nQBDXcLZRTcVZF0NS/6yZ3JO03i/Yv+C1CQRvPgmJk"))
        byte[] salt = decode("/bvrxpG04bMH2j98Sgm5ug")
        byte[] info = "test-info".getBytes(StandardCharsets.UTF_8)
        String expectedResult = "PtiIWzWkNywvjlnyv60Rtz2Zr7vQsgZivlj0Ys9HDy4"

        byte[] result = new BouncyCastleV1LocalCryptoProvider().hkdfSha384(secretKey, salt, info)
        assertThat encodeToString(result), equalTo(expectedResult)
    }

    private static String encodeToString(byte[] bytes) {
        return Base64.getEncoder().withoutPadding().encodeToString(bytes)
    }

    private static byte[] decode(String input) {
        return Base64.getDecoder().decode(input)
    }
}
