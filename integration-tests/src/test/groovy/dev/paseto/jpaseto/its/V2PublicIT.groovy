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
package dev.paseto.jpaseto.its

import dev.paseto.jpaseto.*
import dev.paseto.jpaseto.lang.Keys
import org.apache.commons.codec.binary.Hex
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.security.PrivateKey
import java.security.PublicKey

import static dev.paseto.jpaseto.its.Util.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class V2PublicIT {

    @Test
    void invalidPublicKeyDecode() {

        PublicKey wrongPublicKey = Keys.ed25519PublicKey(Hex.decodeHex('1111111111111111111111111111111111111111111111111111111111111111'))

        def token = 'v2.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9HQr8URrGntTu7Dz9J2IF23d1M7-9lH9xiqdGyJNvzp4angPW5Esc7C5huy_M8I8_DjJK2ZXC2SUYuOFM-Q_5Cw'

            PasetoParser parser = Pasetos.parserBuilder()
                .setClock(clockForVectors())
                .setPublicKey(wrongPublicKey) // this was signed with a different key
                .build()

        // an incorrect key will generate a different auth key, and will fail before the cipher
        expect PasetoSignatureException, {
            parser.parse(token)
        }
    }

    @Test(dataProvider = "officialV2PublicTestVectors")
    void officialVectorsV2PublicVectorsTest(String expectedToken, PublicKey publicKey, PrivateKey privateKey, Map<String, Object> claims, Map<String, Object> footer, String name) {

        // create to token
        PasetoV2PublicBuilder builder = Pasetos.v2Public().builder()
            .setPrivateKey(privateKey)

        claims.forEach { key, value ->
            builder.claim(key, value)
        }

        if (footer != null) {
            footer.forEach { key, value ->
                builder.footerClaim(key, value)
            }
        }

        String token = builder.compact()
        assertThat(token, is(expectedToken))

        // parse the token
        Paseto result = Pasetos.parserBuilder()
            .setClock(clockForVectors())
            .setPublicKey(publicKey)
            .build()
            .parse(token)

        // match expected
        assertThat(result, PasetoMatcher.paseto(v2PublicFromClaims(claims, footer)))
    }

    @DataProvider
    Object[][] officialV2PublicTestVectors() {

        PrivateKey privateKey = Keys.ed25519PrivateKey(Hex.decodeHex('b4cbfb43df4ce210727d953e4a713307fa19bb7d9f85041438d9e11b942a3774' +
                                                                       '1eb9dbbbbc047c03fd70604e0071f0987e16b28b757225c11f00415d0e20b1a2'))

        PublicKey publicKey = Keys.ed25519PublicKey(Hex.decodeHex('1eb9dbbbbc047c03fd70604e0071f0987e16b28b757225c11f00415d0e20b1a2'))

        return [
                ['v2.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9HQr8URrGntTu7Dz9J2IF23d1M7-9lH9xiqdGyJNvzp4angPW5Esc7C5huy_M8I8_DjJK2ZXC2SUYuOFM-Q_5Cw',
                publicKey,
                privateKey,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                null,
                'Test Vector 2-S-1'],

                ['v2.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9flsZsx_gYCR0N_Ec2QxJFFpvQAs7h9HtKwbVK2n1MJ3Rz-hwe8KUqjnd8FAnIJZ601tp7lGkguU63oGbomhoBw.eyJraWQiOiJ6VmhNaVBCUDlmUmYyc25FY1Q3Z0ZUaW9lQTlDT2NOeTlEZmdMMVc2MGhhTiJ9',
                publicKey,
                privateKey,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                [kid: 'zVhMiPBP9fRf2snEcT7gFTioeA9COcNy9DfgL1W60haN'],
                'Test Vector 2-S-2'],
        ]
    }
}
