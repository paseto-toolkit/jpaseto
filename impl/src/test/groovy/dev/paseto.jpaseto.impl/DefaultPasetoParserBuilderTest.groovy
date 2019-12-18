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

import dev.paseto.jpaseto.*
import dev.paseto.jpaseto.io.Deserializer
import dev.paseto.jpaseto.lang.DescribedPredicate
import org.hamcrest.Matcher
import org.testng.annotations.DataProvider
import org.testng.annotations.Test

import java.security.KeyPair
import java.security.KeyPairGenerator
import java.security.PublicKey
import java.time.Clock
import java.time.Duration
import java.time.Instant
import java.time.temporal.ChronoUnit

import static dev.paseto.jpaseto.impl.Util.expect
import static java.nio.charset.StandardCharsets.UTF_8
import static org.hamcrest.MatcherAssert.assertThat
import static org.hamcrest.Matchers.*
import static org.mockito.Mockito.mock

class DefaultPasetoParserBuilderTest {

    KeyPair keyPair = KeyPairGenerator.getInstance("RSA").generateKeyPair()

    DefaultPasetoParserBuilderTest() {
        BouncyCastleInitializer.enableBouncyCastle()
    }

    @Test
    void unsupportedVersionTest() {
        String input = "v5.local.something"

        def parser = Pasetos.parserBuilder()
                    .setPublicKey(keyPair.getPublic())
                    .build()

        expect UnsupportedPasetoException, {
            parser.parse(input) }
    }

    @Test
    void unsupportedPurposeTest() {
        String input = "v1.other.something"

        expect UnsupportedPasetoException, {
            Pasetos.parserBuilder()
                    .setPublicKey(keyPair.getPublic())
                    .build()
                    .parse(input)
        }
    }

    @Test
    void basicUsageTest() {

        def publicKey = mock(PublicKey)
        def secret = "a-secret".getBytes(UTF_8)
        def clock = mock(Clock)
        def skew = Duration.ofSeconds(101)
        def deserializer = mock(Deserializer)

        PasetoParser parser =  new DefaultPasetoParserBuilder()
            .setSharedSecret(secret)
            .setPublicKey(publicKey)
            .setClock(clock)
            .setAllowedClockSkew(skew)
            .setDeserializer(deserializer)
            .build()

        assertThat parser.clock, is(clock)
        assertThat parser.allowedClockSkew, is(skew)
        assertThat parser.deserializer, is(deserializer)
        assertThat parser.keyResolver.publicKey, is(publicKey)
        assertThat parser.keyResolver.sharedSecret.encoded, is(secret)
    }

    @Test
    void requiredClaimsTest() {

        def publicKey = mock(PublicKey)
        def iat = Instant.now()
        def deserializer = mock(Deserializer)
        def exp = iat.plus(1, ChronoUnit.DAYS)
        def nbf = iat.minus(1, ChronoUnit.MINUTES)

        DefaultPasetoParser parser =  new DefaultPasetoParserBuilder()
            .setPublicKey(publicKey)
            .setDeserializer(deserializer)
            .requireIssuedAt(iat)
            .requireExpiration(exp)
            .requireNotBefore(nbf)
            .requireIssuer("issuer1")
            .requireAudience("audience1")
            .requireSubject("subject1")
            .requireTokenId("tokenId1")
            .requireKeyId("keyId1")
            .require("foobar", DescribedPredicate.equalTo("FOO"))
            .build()

        assertThat parser.userExpectedClaimsMap, allOf(
                hasPredicateEntry("iat", "equal to: '${iat}'"),
                hasPredicateEntry("exp", "equal to: '${exp}'"),
                hasPredicateEntry("nbf", "equal to: '${nbf}'")
        ,
                hasPredicateEntry("iss", "equal to: 'issuer1'"),
                hasPredicateEntry("aud", "equal to: 'audience1'"),
                hasPredicateEntry("sub", "equal to: 'subject1'"),
                hasPredicateEntry("jti", "equal to: 'tokenId1'"),
//                hasPredicateEntry("kid", "equal to: 'keyId1'"), // not sure how/if this one would work // FIXME
                hasPredicateEntry("foobar", "equal to: 'FOO'"))
    }

    Matcher<Map> hasPredicateEntry(String key, String description) {
        hasEntry(is(key), hasProperty("description", is(description.toString()))) // gstring to regular string
    }

    @Test
    void invalidNotBeforeTest() {
        String token = Pasetos.v1Public().builder()
            .setPrivateKey(keyPair.getPrivate())
            .setNotBefore(Instant.now().plus(1, ChronoUnit.HOURS))
            .compact()

        expect PrematurePasetoException, { Pasetos.parserBuilder()
            .setPublicKey(keyPair.getPublic())
            .build()
            .parse(token) }
    }

    @Test
    void invalidExpireTest() {
        String token = Pasetos.v1Public().builder()
            .setPrivateKey(keyPair.getPrivate())
            .setExpiration(Instant.now().minus(1, ChronoUnit.HOURS))
            .compact()

        expect ExpiredPasetoException, { Pasetos.parserBuilder()
            .setPublicKey(keyPair.getPublic())
            .build()
            .parse(token)
        }
    }

    @Test
    void requireKeyIdTest() {
        String token = Pasetos.v1Public().builder()
            .setPrivateKey(keyPair.getPrivate())
            .setKeyId("invalid")
            .compact()

        PasetoParser parser = Pasetos.parserBuilder()
            .setPublicKey(keyPair.getPublic())
            .requireKeyId("Valid")
            .build()

        def e = expect IncorrectClaimException, { parser.parse(token) }
        assertThat e.getMessage(), startsWith("Expected 'kid' claim to be equal to: 'Valid',")
    }

    @Test
    void missingKeyIdTest() {
        String token = Pasetos.v1Public().builder()
            .setPrivateKey(keyPair.getPrivate())
            .compact()

        PasetoParser parser = Pasetos.parserBuilder()
            .setPublicKey(keyPair.getPublic())
            .requireKeyId("Valid")
            .build()

        def e = expect MissingClaimException, { parser.parse(token) }
        assertThat e.getMessage(), startsWith("Expected 'kid' claim to be equal to: 'Valid',")
    }

    @Test(dataProvider = "requireClaims")
    void incorrectClaimTest(String claimName, Object value, Object invalidValue, Closure<PasetoParserBuilder> closure, Matcher<String> exceptionMessageMatcher) {
        String token = Pasetos.v1Public().builder()
            .setPrivateKey(keyPair.getPrivate())
            .claim(claimName, invalidValue)
            .compact()

        PasetoParserBuilder parserBuilder = Pasetos.parserBuilder()
            .setPublicKey(keyPair.getPublic())
        closure.call(parserBuilder)
        PasetoParser parser = parserBuilder.build()

        def e = expect IncorrectClaimException, { parser.parse(token) }
        assertThat e.getMessage(), exceptionMessageMatcher
    }

    @Test(dataProvider = "requireClaims")
    void missingClaimTest(String claimName, Object value, Object invalidValue, Closure<PasetoParserBuilder> closure, Matcher<String> exceptionMessageMatcher) {
        String token = Pasetos.v1Public().builder()
            .setPrivateKey(keyPair.getPrivate())
            .compact()

        PasetoParserBuilder parserBuilder = Pasetos.parserBuilder()
            .setPublicKey(keyPair.getPublic())
        closure.call(parserBuilder)
        PasetoParser parser = parserBuilder.build()

        def e = expect MissingClaimException, { parser.parse(token) }
        assertThat e.getMessage(), exceptionMessageMatcher
    }

    @DataProvider
    Object[][] requireClaims() {

        Instant now = Instant.now()

        return [
                ["iss", "Valid", "invalid", { it.requireIssuer("Valid") }, startsWith("Expected 'iss' claim to be equal to: 'Valid',")],
                ["aud", "Valid", "invalid", { it.requireAudience("Valid") }, startsWith("Expected 'aud' claim to be equal to: 'Valid',")],
                ["sub", "Valid", "invalid", { it.requireSubject("Valid") }, startsWith("Expected 'sub' claim to be equal to: 'Valid',")],
                ["jti", "Valid", "invalid", { it.requireTokenId("Valid") }, startsWith("Expected 'jti' claim to be equal to: 'Valid',")],
                ["foobar", "Valid", "invalid", { it.require("foobar", "Valid") }, startsWith("Expected 'foobar' claim to be equal to: 'Valid',")],
                ["iat", now, now.plus(1, ChronoUnit.DAYS), { it.requireIssuedAt(now) }, startsWith("Expected 'iat' claim to be equal to: '${now}',")],
                ["exp", now, now.plus(1, ChronoUnit.DAYS), { it.requireExpiration(now) }, startsWith("Expected 'exp' claim to be equal to: '${now}',")],
                ["nbf", now, now.minus(1, ChronoUnit.DAYS), { it.requireNotBefore(now) }, startsWith("Expected 'nbf' claim to be equal to: '${now}',")],
        ]
    }

}
