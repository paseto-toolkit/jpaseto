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
package dev.paseto.jpaseto

import org.mockito.Mockito
import org.testng.annotations.Test

import java.time.Instant

import static org.mockito.Mockito.verify

class ClaimsMutatorTest {

    @Test
    void setIssuer() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = "test-issuer"
        claimsMutator.setIssuer(expectedValue)
        verify(claimsMutator).claim("iss", expectedValue)
    }

    @Test
    void setAudience() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = "test-audience"
        claimsMutator.setAudience(expectedValue)
        verify(claimsMutator).claim("aud", expectedValue)
    }

    @Test
    void setIssuedAt() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = Instant.now()
        claimsMutator.setIssuedAt(expectedValue)
        verify(claimsMutator).claim("iat", expectedValue)
    }

    @Test
    void setTokenId() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = "test-tokenId"
        claimsMutator.setTokenId(expectedValue)
        verify(claimsMutator).claim("jti", expectedValue)
    }

    @Test
    void setSubject() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = "test-subject"
        claimsMutator.setSubject(expectedValue)
        verify(claimsMutator).claim("sub", expectedValue)
    }

    @Test
    void setExpiration() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = Instant.now()
        claimsMutator.setExpiration(expectedValue)
        verify(claimsMutator).claim("exp", expectedValue)
    }

    @Test
    void setNotBefore() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = Instant.now()
        claimsMutator.setNotBefore(expectedValue)
        verify(claimsMutator).claim("nbf", expectedValue)
    }

    @Test
    void setKeyId() {
        def claimsMutator =  Mockito.spy(ClaimsMutator)
        def expectedValue = "test-keyId"
        claimsMutator.setKeyId(expectedValue)
        verify(claimsMutator).footerClaim("kid", expectedValue)
    }
}
