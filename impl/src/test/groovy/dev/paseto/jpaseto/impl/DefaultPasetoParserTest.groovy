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
package dev.paseto.jpaseto.impl

import dev.paseto.jpaseto.PasetoParser
import dev.paseto.jpaseto.Pasetos
import dev.paseto.jpaseto.lang.Keys
import org.apache.commons.codec.binary.Hex
import org.mockito.Mockito
import org.testng.annotations.Test

import javax.crypto.SecretKey
import java.security.PublicKey

import static dev.paseto.jpaseto.impl.Util.clockForVectors
import static dev.paseto.jpaseto.impl.Util.expect
import static org.mockito.Mockito.mock

class DefaultPasetoParserTest {

    DefaultPasetoParserTest() {
        BouncyCastleInitializer.enableBouncyCastle()
    }

    @Test
    void parseTokenWithoutPublicKey() {
        def token = "v1.public.eyJkYXRhIjoidGhpcyBpcyBhIHNpZ25lZCBtZXNzYWdlIiwiZXhwIjoiMjAxOS0wMS0wMVQwMDowMDowMCswMDowMCJ9cIZKahKeGM5kiAS_4D70Qbz9FIThZpxetJ6n6E6kXP_119SvQcnfCSfY_gG3D0Q2v7FEtm2Cmj04lE6YdgiZ0RwA41WuOjXq7zSnmmHK9xOSH6_2yVgt207h1_LphJzVztmZzq05xxhZsV3nFPm2cCu8oPceWy-DBKjALuMZt_Xj6hWFFie96SfQ6i85lOsTX8Kc6SQaG-3CgThrJJ6W9DC-YfQ3lZ4TJUoY3QNYdtEgAvp1QuWWK6xmIb8BwvkBPej5t88QUb7NcvZ15VyNw3qemQGn2ITSdpdDgwMtpflZOeYdtuxQr1DSGO2aQyZl7s0WYn1IjdQFx6VjSQ4yfw"

        SecretKey sharedSecret = Keys.secretKey()

        PasetoParser parser = Pasetos.parserBuilder()
            .setClock(clockForVectors())
            .setSharedSecret(sharedSecret) // a public key should be set for this type of token
            .build()

        // wrong type of key configured
        expect IllegalArgumentException, { parser.parse(token) }
    }

    @Test
    void parseTokenWithoutSharedSecret() {
        def token = 'v1.local.WzhIh1MpbqVNXNt7-HbWvL-JwAym3Tomad9Pc2nl7wK87vGraUVvn2bs8BBNo7jbukCNrkVID0jCK2vr5bP18G78j1bOTbBcP9HZzqnraEdspcjd_PvrxDEhj9cS2MG5fmxtvuoHRp3M24HvxTtql9z26KTfPWxJN5bAJaAM6gos8fnfjJO8oKiqQMaiBP_Cqncmqw8'

        PublicKey publicKey = mock(PublicKey)

        PasetoParser parser = Pasetos.parserBuilder()
            .setClock(clockForVectors())
            .setPublicKey(publicKey) // a shared secret
            .build()

        // wrong type of key configured
        expect IllegalArgumentException, { parser.parse(token) }
    }
}
