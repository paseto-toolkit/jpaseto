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

import javax.crypto.SecretKey

import static dev.paseto.jpaseto.its.Util.*
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

/**
 * Standard vector tests from paseto.io
 */
class V2LocalIT {

    @Test
    void invalidSecretDecode() {
        SecretKey sharedSecret = Keys.secretKey(Hex.decodeHex('1111111111111111111111111111111111111111111111111111111111111111'))
        def token = 'v2.local.97TTOvgwIxNGvV80XKiGZg_kD3tsXM_-qB4dZGHOeN1cTkgQ4PnW8888l802W8d9AvEGnoNBY3BnqHORy8a5cC8aKpbA0En8XELw2yDk2f1sVODyfnDbi6rEGMY3pSfCbLWMM2oHJxvlEl2XbQ'

        PasetoParser parser = Pasetos.parserBuilder()
                .setClock(clockForVectors())
                .setSharedSecret(sharedSecret)
                .build()

        // an incorrect key will generate a different auth key, and will fail before the cipher
        expect InvalidMacException, { parser.parse(token) }
    }

    @Test(dataProvider = "officialV2LocalTestVectors")
    void officialVectorsV2LocalDecodeTest(String token, SecretKey secretKey, Map<String, Object> claims, Map<String, Object> footer, String name) {

        PasetoParser parser = Pasetos.parserBuilder()
                .setClock(clockForVectors())
                .setSharedSecret(secretKey)
                .build()

        Paseto result = parser.parse(token)
        assertThat result, PasetoMatcher.paseto(v2LocalFromClaims(claims, footer))
    }

    @Test(dataProvider = "officialV2LocalTestVectors")
    void officialVectorsV2LocalEncodeTest(String token, SecretKey secretKey, Map<String, Object> claims, Map<String, Object> footer, String name) {

        PasetoBuilder pasetoBuilder = Pasetos.v2Local().builder().setSharedSecret(secretKey)

        claims.forEach { key, value ->
            pasetoBuilder.claim(key, value)
        }
        if (footer) {
            footer.forEach { key, value ->
                pasetoBuilder.footerClaim(key, value)
            }
        }

        String result = pasetoBuilder.compact()
        Paseto parsedResult =  Pasetos.parserBuilder()
                .setClock(clockForVectors())
                .setSharedSecret(secretKey)
                .build()
                .parse(result)

        assertThat parsedResult, is(PasetoMatcher.paseto(v2LocalFromClaims(claims, footer)))
    }

    @DataProvider
    Object[][] officialV2LocalTestVectors() {

        SecretKey secretKey = Keys.secretKey(Hex.decodeHex("707172737475767778797a7b7c7d7e7f808182838485868788898a8b8c8d8e8f"))

        return [
                ["v2.local.97TTOvgwIxNGvV80XKiGZg_kD3tsXM_-qB4dZGHOeN1cTkgQ4PnW8888l802W8d9AvEGnoNBY3BnqHORy8a5cC8aKpbA0En8XELw2yDk2f1sVODyfnDbi6rEGMY3pSfCbLWMM2oHJxvlEl2XbQ",
                secretKey,
                [
                    data: "this is a signed message",
                    exp: "2019-01-01T00:00:00+00:00"
                ],
                null,
                "Test Vector 2-E-1"
                ],

                ["v2.local.CH50H-HM5tzdK4kOmQ8KbIvrzJfjYUGuu5Vy9ARSFHy9owVDMYg3-8rwtJZQjN9ABHb2njzFkvpr5cOYuRyt7CRXnHt42L5yZ7siD-4l-FoNsC7J2OlvLlIwlG06mzQVunrFNb7Z3_CHM0PK5w",
                secretKey,
                [
                    data: "this is a secret message",
                    exp: "2019-01-01T00:00:00+00:00"
                ],
                null,
                "Test Vector 2-E-2"
                ],

                ["v2.local.5K4SCXNhItIhyNuVIZcwrdtaDKiyF81-eWHScuE0idiVqCo72bbjo07W05mqQkhLZdVbxEa5I_u5sgVk1QLkcWEcOSlLHwNpCkvmGGlbCdNExn6Qclw3qTKIIl5-O5xRBN076fSDPo5xUCPpBA",
                secretKey,
                [
                    data: "this is a signed message",
                    exp: "2019-01-01T00:00:00+00:00"
                ],
                null,
                "Test Vector 2-E-3"
                ],

                ["v2.local.pvFdDeNtXxknVPsbBCZF6MGedVhPm40SneExdClOxa9HNR8wFv7cu1cB0B4WxDdT6oUc2toyLR6jA6sc-EUM5ll1EkeY47yYk6q8m1RCpqTIzUrIu3B6h232h62DPbIxtjGvNRAwsLK7LcV8oQ",
                secretKey,
                [
                    data: "this is a secret message",
                    exp: "2019-01-01T00:00:00+00:00"
                ],
                null,
                "Test Vector 2-E-4"
                ],

                ["v2.local.5K4SCXNhItIhyNuVIZcwrdtaDKiyF81-eWHScuE0idiVqCo72bbjo07W05mqQkhLZdVbxEa5I_u5sgVk1QLkcWEcOSlLHwNpCkvmGGlbCdNExn6Qclw3qTKIIl5-zSLIrxZqOLwcFLYbVK1SrQ.eyJraWQiOiJ6VmhNaVBCUDlmUmYyc25FY1Q3Z0ZUaW9lQTlDT2NOeTlEZmdMMVc2MGhhTiJ9",
                secretKey,
                [
                    data: "this is a signed message",
                    exp: "2019-01-01T00:00:00+00:00"
                ],
                [kid: "zVhMiPBP9fRf2snEcT7gFTioeA9COcNy9DfgL1W60haN"],
                "Test Vector 2-E-5"
                ],

                ["v2.local.pvFdDeNtXxknVPsbBCZF6MGedVhPm40SneExdClOxa9HNR8wFv7cu1cB0B4WxDdT6oUc2toyLR6jA6sc-EUM5ll1EkeY47yYk6q8m1RCpqTIzUrIu3B6h232h62DnMXKdHn_Smp6L_NfaEnZ-A.eyJraWQiOiJ6VmhNaVBCUDlmUmYyc25FY1Q3Z0ZUaW9lQTlDT2NOeTlEZmdMMVc2MGhhTiJ9",
                secretKey,
                [
                    data: "this is a secret message",
                    exp: "2019-01-01T00:00:00+00:00"
                ],
                [kid: "zVhMiPBP9fRf2snEcT7gFTioeA9COcNy9DfgL1W60haN"],
                "Test Vector 2-E-6"
                ]
        ]
    }
}
