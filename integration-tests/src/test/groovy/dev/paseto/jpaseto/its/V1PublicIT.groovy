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
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.security.*
import java.security.spec.PKCS8EncodedKeySpec
import java.security.spec.X509EncodedKeySpec

import static dev.paseto.jpaseto.its.Util.*
import static org.hamcrest.MatcherAssert.assertThat

class V1PublicIT {

    KeyPair keyPairForNegativeTests = KeyPairGenerator.getInstance("RSA").generateKeyPair()

    @Test
    void invalidPublicKeyDecode() {

        def token = 'v1.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9cIZKahKeGM5kiAS_4D70Qbz9FIThZpxetJ6n6E6kXP_119SvQcnfCSfY_gG3D0Q2v7FEtm2Cmj04lE6YdgiZ0RwA41WuOjXq7zSnmmHK9xOSH6_2yVgt207h1_LphJzVztmZzq05xxhZsV3nFPm2cCu8oPceWy-DBKjALuMZt_Xj6hWFFie96SfQ6i85lOsTX8Kc6SQaG-3CgThrJJ6W9DC-YfQ3lZ4TJUoY3QNYdtEgAvp1QuWWK6xmIb8BwvkBPej5t88QUb7NcvZ15VyNw3qemQGn2ITSdpdDgwMtpflZOeYdtuxQr1DSGO2aQyZl7s0WYn1IjdQFx6VjSQ4yfw'

        PasetoParser parser = Pasetos.parserBuilder()
            .setClock(clockForVectors())
            .setPublicKey(keyPairForNegativeTests.getPublic()) // this was signed with a different key
            .build()

        // an incorrect key will generate a different auth key, and will fail before the cipher
        expect PasetoSignatureException, { parser.parse(token) }
    }

    @Test(dataProvider = "officialV1PublicTestVectors")
    void officialVectorsV2PublicVectorsTest(String expectedToken, PublicKey publicKey, PrivateKey privateKey, Map<String, Object> claims, Map<String, Object> footer, String name) {

        // create to token
        PasetoV1PublicBuilder builder = Pasetos.V1.PUBLIC.builder()
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

        // parse the token
        Paseto parsedToken = Pasetos.parserBuilder()
            .setClock(clockForVectors())
            .setPublicKey(publicKey)
            .build()
            .parse(token)

        Paseto parsedExpectedToken = Pasetos.parserBuilder()
            .setClock(clockForVectors())
            .setPublicKey(publicKey)
            .build()
            .parse(token)

        // match expected
        assertThat(parsedToken, PasetoMatcher.paseto(v1PublicFromClaims(claims, footer)))
        assertThat(parsedToken, PasetoMatcher.paseto(parsedExpectedToken))
    }

    @DataProvider
    Object[][] officialV1PublicTestVectors() {

        String publicKeyPem =
                "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAyaTgTt53ph3p5GHgwoGW" +
                "wz5hRfWXSQA08NCOwe0FEgALWos9GCjNFCd723nCHxBtN1qd74MSh/uN88JPIbwx" +
                "KheDp4kxo4YMN5trPaF0e9G6Bj1N02HnanxFLW+gmLbgYO/SZYfWF/M8yLBcu5Y1" +
                "Ot0ZxDDDXS9wIQTtBE0ne3YbxgZJAZTU5XqyQ1DxdzYyC5lF6yBaR5UQtCYTnXAA" +
                "pVRuUI2Sd6L1E2vl9bSBumZ5IpNxkRnAwIMjeTJB/0AIELh0mE5vwdihOCbdV6al" +
                "UyhKC1+1w/FW6HWcp/JG1kKC8DPIidZ78Bbqv9YFzkAbNni5eSBOsXVBKG78Zsc8" +
                "owIDAQAB"

        // converted format from spec:
        // openssl pkcs8 -topk8 -inform PEM -outform PEM -in spec.pem -out 8pkcs8.pem -nocrypt
        String privateKeyPem =
                "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQDJpOBO3nemHenk" +
                "YeDCgZbDPmFF9ZdJADTw0I7B7QUSAAtaiz0YKM0UJ3vbecIfEG03Wp3vgxKH+43z" +
                "wk8hvDEqF4OniTGjhgw3m2s9oXR70boGPU3TYedqfEUtb6CYtuBg79Jlh9YX8zzI" +
                "sFy7ljU63RnEMMNdL3AhBO0ETSd7dhvGBkkBlNTlerJDUPF3NjILmUXrIFpHlRC0" +
                "JhOdcAClVG5QjZJ3ovUTa+X1tIG6Znkik3GRGcDAgyN5MkH/QAgQuHSYTm/B2KE4" +
                "Jt1XpqVTKEoLX7XD8VbodZyn8kbWQoLwM8iJ1nvwFuq/1gXOQBs2eLl5IE6xdUEo" +
                "bvxmxzyjAgMBAAECggEAXbaMsNrfjIp2ezep93u2j4LcPmFHMBwyfoDO9/2pz5XJ" +
                "sQjpGeNMfENlYrkRqNI/j+xDXl7yK9STQmhZ0nnd94v6GdC/CxpvbyCCFKCGvEza" +
                "QbAYDVeA75JVrCom3xKO8T5D7//TVkorQ7IDRwMmNfcv1Gg9Q3+agx4A8XDSGqQU" +
                "SGbvYZJUIRjV5j5w5eYveJzGeyeVQP/9JL2rfdHEXbLmiJbV52pxGqFb/svXJgxv" +
                "EcVRzFaHxZTCOfUACGgI5TMXxkH7Rf88+H7WdiirrHCovRkoPCtDxIOFb+s0Mt47" +
                "2CJzmuXsR61FBFsW+xnjQB2xfbBT6VYVSQHjYCO6gQKBgQDnP8JeoMV9nwxcI8A5" +
                "66LzLkc0O27fRpH4c5ewEOdEjyG5ewY0PTgV+ZdHueSCGvTx8VUOYTg94pSTzXRg" +
                "lv4gAmjzCs5nTrzafrIQw7nyiaoqKf6IhRWs9ctIujkIKGurDYni+DOiXM/Dc3kj" +
                "wUl0uiZv0WStXzgf4i+I8jKXIQKBgQDfOfAAbb1BfoBMR8WkZU5yiCiscKe13ieJ" +
                "pYKrmd7mwUv4TAt5VzXMP3KPVrFwN6UgINnEJnKdTuQOetBwWRPRuMf+ALwmTe/t" +
                "dP2XVQMOoLVvuub9fnAUhn+uY1rWtVmEvWj+0rHm28zazInWr3m/s13KAUgQhb6p" +
                "sgptzpbPQwKBgGxRl1AP6rH/ECEQtffrgjZ6lOvIcxSuz60bKBBWup2Ilfl1wOAz" +
                "VNQmR1BXqMuwqM+zhW3o6BlEyue4syyTTZHczyAZDbmiTh/ifLIRnEYZadW6Ofnk" +
                "rNSJhaEZaaGCnXxQKShhrn39D2yz6ChxX2EH2P1Dje8PzRBSOIXjPQNBAoGALtdB" +
                "fVWJuQyKb3dACdcYNwBLSKP7DTaopUGNweRv2YwGHPwYDEY4i7tklp9ibGHAzJUY" +
                "HQjUVB4RzNgIlQqcFg3oKWyODpucFP/PlsnH8nHWoLNfdSHq8uOmNzmx/gvf1PLJ" +
                "7W7Y1dCZk/AHnH0F1ywUKidKr+zgrUsm1RPcoXECgYEA0ZxDyc5uLebjBE7IquQJ" +
                "ZxbEUUyenDG/Slbvbsef3C5o6zhRt6wKfCalwxN/MZQO7NhcK0CrakmXrgcbrCx2" +
                "RaaMFMkSmbpv2Js4E3eoVXbNDQfLIqUxbEi5VKP2A6jrWEXtQf1cHpHgdF2WkE64" +
                "huABZnjp2SP38cz2i90/QjI="


            KeyFactory keyFactory = KeyFactory.getInstance("RSA")

            PublicKey publicKey = keyFactory.generatePublic(new X509EncodedKeySpec(Base64.decoder.decode(publicKeyPem)))
            PrivateKey privateKey = keyFactory.generatePrivate(new PKCS8EncodedKeySpec(Base64.decoder.decode(privateKeyPem)))

        return [
                ['v1.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9cIZKahKeGM5kiAS_4D70Qbz9FIThZpxetJ6n6E6kXP_119SvQcnfCSfY_gG3D0Q2v7FEtm2Cmj04lE6YdgiZ0RwA41WuOjXq7zSnmmHK9xOSH6_2yVgt207h1_LphJzVztmZzq05xxhZsV3nFPm2cCu8oPceWy-DBKjALuMZt_Xj6hWFFie96SfQ6i85lOsTX8Kc6SQaG-3CgThrJJ6W9DC-YfQ3lZ4TJUoY3QNYdtEgAvp1QuWWK6xmIb8BwvkBPej5t88QUb7NcvZ15VyNw3qemQGn2ITSdpdDgwMtpflZOeYdtuxQr1DSGO2aQyZl7s0WYn1IjdQFx6VjSQ4yfw',
                publicKey,
                privateKey,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                null,
                'Test Vector s-S-1'],

                ['v1.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9sBTIb0J_4misAuYc4-6P5iR1rQighzktpXhJ8gtrrp2MqSSDkbb8q5WZh3FhUYuW_rg2X8aflDlTWKAqJkM3otjYwtmfwfOhRyykxRL2AfmIika_A-_MaLp9F0iw4S1JetQQDV8GUHjosd87TZ20lT2JQLhxKjBNJSwWue8ucGhTgJcpOhXcthqaz7a2yudGyd0layzeWziBhdQpoBR6ryTdtIQX54hP59k3XCIxuYbB9qJMpixiPAEKBcjHT74sA-uukug9VgKO7heWHwJL4Rl9ad21xyNwaxAnwAJ7C0fN5oGv8Rl0dF11b3tRmsmbDoIokIM0Dba29x_T3YzOyg.eyJraWQiOiJkWWtJU3lseFFlZWNFY0hFTGZ6Rjg4VVpyd2JMb2xOaUNkcHpVSEd3OVVxbiJ9',
                publicKey,
                privateKey,
                [data: 'this is a signed message', exp: '2019-01-01T00:00:00+00:00'],
                [kid: 'dYkISylxQeecEcHELfzF88UZrwbLolNiCdpzUHGw9Uqn'],
                'Test Vector 1-S-2'],
        ]
    }
}
