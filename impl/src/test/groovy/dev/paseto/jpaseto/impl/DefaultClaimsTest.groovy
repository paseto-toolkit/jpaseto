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

import org.testng.annotations.Test

import java.time.Instant

import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is
import static org.hamcrest.Matchers.nullValue

class DefaultClaimsTest {

    @Test
    void getIssuer_null() {
        def claims = new DefaultClaims([ iss: null ])
        assertThat claims.getIssuer(), nullValue()
    }

    @Test
    void getIssuer() {
        def expectedValue = "test-issuer"
        def claims = new DefaultClaims([ iss: expectedValue ])
        assertThat claims.getIssuer(), is(expectedValue)
    }

    @Test
    void getSubject() {
        def expectedValue = "test-subject"
        def claims = new DefaultClaims([ sub: expectedValue ])
        assertThat claims.getSubject(), is(expectedValue)
    }

    @Test
    void getAudience() {
        def expectedValue = "test-audience"
        def claims = new DefaultClaims([ aud: expectedValue ])
        assertThat claims.getAudience(), is(expectedValue)
    }

    @Test
    void getExpiration() {
        def expectedValue = Instant.now()
        def claims = new DefaultClaims([ exp: expectedValue ])
        assertThat claims.getExpiration(), is(expectedValue)
    }

    @Test
    void getNotBefore() {
        def expectedValue = Instant.now()
        def claims = new DefaultClaims([ nbf: expectedValue ])
        assertThat claims.getNotBefore(), is(expectedValue)
    }

    @Test
    void getIssuedAt() {
        def expectedValue = Instant.now()
        def claims = new DefaultClaims([ iat: expectedValue ])
        assertThat claims.getIssuedAt(), is(expectedValue)
    }

    @Test
    void getTokenId() {
        def expectedValue = "test-tokenId"
        def claims = new DefaultClaims([ jti: expectedValue ])
        assertThat claims.getTokenId(), is(expectedValue)
    }
}
