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

import dev.paseto.jpaseto.lang.Keys
import org.testng.annotations.Test
import java.security.KeyPairGenerator

import static dev.paseto.jpaseto.Util.expect
import static java.nio.charset.StandardCharsets.UTF_8
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.is

class KeyResolverAdapterTest {

    KeyResolverAdapterTest() {
        BouncyCastleInitializer.enableBouncyCastle()
    }

    @Test
    void expectUnsupportedPasetoException() {
        KeyResolver keyResolver = new KeyResolverAdapter() {}
        expect UnsupportedPasetoException, { keyResolver.resolvePublicKey(Version.V1, Purpose.PUBLIC, null) }
        expect UnsupportedPasetoException, { keyResolver.resolveSharedKey(Version.V1, Purpose.LOCAL, null) }
    }

    @Test
    void sharedKeyFromBytes() {
        byte[] expectedKey = "my-key".getBytes(UTF_8)

        KeyResolver keyResolver = new KeyResolverAdapter() {
            @Override
            protected byte[] resolveSharedKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
                return expectedKey
            }
        }
        assertThat keyResolver.resolveSharedKey(Version.V1, Purpose.LOCAL, null), is(Keys.secretKey(expectedKey))
    }

    @Test
    void resolvePublicKeyBytes_v1() {
        def keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()
        def pubKeyBytes = keyPair.getPublic().getEncoded()


        KeyResolver keyResolver = new KeyResolverAdapter() {
            @Override
            protected byte[] resolvePublicKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
                return pubKeyBytes
            }
        }
        assertThat keyResolver.resolvePublicKey(Version.V1, Purpose.PUBLIC, null), is(keyPair.getPublic())
    }

    @Test
    void resolvePublicKeyBytes_v1Fail() {
        KeyResolver keyResolver = new KeyResolverAdapter() {
            @Override
            protected byte[] resolvePublicKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
                return new byte[0]
            }
        }
        expect PasetoSignatureException, { keyResolver.resolvePublicKey(Version.V1, Purpose.PUBLIC, null) }
    }

    @Test
    void resolvePublicKeyBytes_v2() {
        def keyPair = KeyPairGenerator.getInstance("Ed25519").generateKeyPair()
        def pubKeyBytes = keyPair.getPublic().getEncoded()


        KeyResolver keyResolver = new KeyResolverAdapter() {
            @Override
            protected byte[] resolvePublicKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
                return pubKeyBytes
            }
        }
        assertThat keyResolver.resolvePublicKey(Version.V2, Purpose.PUBLIC, null), is(keyPair.getPublic())
    }

    @Test
    void resolvePublicKeyBytes_v2Fail() {
        KeyResolver keyResolver = new KeyResolverAdapter() {
            @Override
            protected byte[] resolvePublicKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
                return [ 0, 0, 0, 0, 0, 0, 0, 0, 0 ] // BC attempts to read [8], and will fail with an index error if the array isn't long enough
            }
        }
        expect PasetoSignatureException, { keyResolver.resolvePublicKey(Version.V2, Purpose.PUBLIC, null) }
    }
}
