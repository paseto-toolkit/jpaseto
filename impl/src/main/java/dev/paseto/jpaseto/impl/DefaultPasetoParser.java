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
package dev.paseto.jpaseto.impl;

import dev.paseto.jpaseto.ClaimPasetoException;
import dev.paseto.jpaseto.ExpiredPasetoException;
import dev.paseto.jpaseto.FooterClaims;
import dev.paseto.jpaseto.IncorrectClaimException;
import dev.paseto.jpaseto.InvalidClaimException;
import dev.paseto.jpaseto.KeyResolver;
import dev.paseto.jpaseto.MissingClaimException;
import dev.paseto.jpaseto.Paseto;
import dev.paseto.jpaseto.PasetoParser;
import dev.paseto.jpaseto.PasetoSignatureException;
import dev.paseto.jpaseto.PrematurePasetoException;
import dev.paseto.jpaseto.Purpose;
import dev.paseto.jpaseto.UnsupportedPasetoException;
import dev.paseto.jpaseto.Version;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V2LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V2PublicCryptoProvider;
import dev.paseto.jpaseto.io.Deserializer;
import dev.paseto.jpaseto.lang.Assert;
import dev.paseto.jpaseto.lang.DateFormats;
import dev.paseto.jpaseto.lang.DescribedPredicate;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.security.PublicKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.Base64;
import java.util.Collections;
import java.util.Map;
import java.util.function.Predicate;

class DefaultPasetoParser implements PasetoParser {

    private final V1LocalCryptoProvider v1LocalCryptoProvider;
    private final V2LocalCryptoProvider v2LocalCryptoProvider;
    private final V1PublicCryptoProvider v1PublicCryptoProvider;
    private final V2PublicCryptoProvider v2PublicCryptoProvider;
    private final KeyResolver keyResolver;
    private final Deserializer<Map<String, Object>> deserializer;
    private final Clock clock;
    private final Duration allowedClockSkew;
    private final Map<String, Predicate<Object>> userExpectedClaimsMap;
    private final Map<String, Predicate<Object>> userExpectedFooterClaimsMap;

    DefaultPasetoParser(V1LocalCryptoProvider v1LocalCryptoProvider, V2LocalCryptoProvider v2LocalCryptoProvider, V1PublicCryptoProvider v1PublicCryptoProvider, V2PublicCryptoProvider v2PublicCryptoProvider, KeyResolver keyResolver, Deserializer<Map<String, Object>> deserializer, Clock clock, Duration allowedClockSkew, Map<String, Predicate<Object>> expectedClaimsMap, Map<String, Predicate<Object>> expectedFooterClaimsMap) {
        this.v1LocalCryptoProvider = v1LocalCryptoProvider;
        this.v2LocalCryptoProvider = v2LocalCryptoProvider;
        this.v1PublicCryptoProvider = v1PublicCryptoProvider;
        this.v2PublicCryptoProvider = v2PublicCryptoProvider;
        this.keyResolver = keyResolver;
        this.deserializer = deserializer;
        this.clock = clock;
        this.allowedClockSkew = allowedClockSkew;
        this.userExpectedClaimsMap = Collections.unmodifiableMap(expectedClaimsMap);
        this.userExpectedFooterClaimsMap = Collections.unmodifiableMap(expectedFooterClaimsMap);
    }

    @Override
    public Paseto parse(String token) {
        Assert.hasText(token, "Paseto token cannot be null or empty");

        String[] parts = token.split("\\.");
        Assert.isTrue(parts.length == 3 || parts.length == 4, "Paseto token expected to have 3 or 4 parts."); // header is optional
        // format is <version>.<purpose>.<payload>[.<footer>]

        Version version = Version.from(parts[0]);
        Purpose purpose = Purpose.from(parts[1]);
        byte[] payloadBytes = Base64.getUrlDecoder().decode(parts[2].getBytes(StandardCharsets.UTF_8));
        byte[] footerBytes = parts.length == 4 ? Base64.getUrlDecoder().decode(parts[3].getBytes(StandardCharsets.UTF_8)) : new byte[0];

        Paseto paseto;
        if (version == Version.V2 && purpose == Purpose.LOCAL) {
            paseto = v2Local(payloadBytes, footerBytes);
        } else if (version == Version.V2 && purpose == Purpose.PUBLIC) {
            paseto = v2Public(payloadBytes, footerBytes);
        } else if (version == Version.V1 && purpose == Purpose.LOCAL) {
            paseto = v1Local(payloadBytes, footerBytes);
        } else if (version == Version.V1 && purpose == Purpose.PUBLIC) {
            paseto = v1Public(payloadBytes, footerBytes);
        } else {
            // Cannot reach this point unless the Version and/or Purpose enum have been changed
            // parsing those enums will fail before this point
            throw new UnsupportedPasetoException("Paseto token with header: '" + version.toString() + "." + purpose.toString() +".' is not supported.");
        }

        verifyExpiration(paseto);
        verifyNotBefore(paseto);
        validateExpectedClaims(paseto);
        validateExpectedFooterClaims(paseto);

        return paseto;
    }

    private Paseto v2Local(byte[] encryptedBytes, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        SecretKey sharedSecret = keyResolver.resolveSharedKey(Version.V2, Purpose.LOCAL, footer);
        Assert.notNull(sharedSecret, "A shared secret could not be resolved.  A shared secret must be configured in " +
                "'Pasetos.parserBuilder().setSharedSecret(...)' or Pasetos.parserBuilder().setKeyResolver(...)");
        byte[] payload = v2LocalCryptoProvider.decrypt(encryptedBytes, footerBytes, sharedSecret);
        Map<String, Object> claims = deserializer.deserialize(payload);
        return new DefaultPaseto(Version.V2, Purpose.LOCAL, new DefaultClaims(claims), footer);
    }

    private Paseto v1Public(byte[] payload, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        // 3
        byte[] message = Arrays.copyOf(payload, payload.length - 256);
        byte[] signature = Arrays.copyOfRange(payload, payload.length - 256, payload.length);

        PublicKey publicKey = keyResolver.resolvePublicKey(Version.V1, Purpose.PUBLIC, footer);
        Assert.notNull(publicKey, "A public key could not be resolved.  A public key must be configured in " +
                "'Pasetos.parserBuilder().setPublicKey(...)' or Pasetos.parserBuilder().setKeyResolver(...)");
        boolean valid = v1PublicCryptoProvider.verify(message, footerBytes, signature, publicKey);
        if (!valid) {
            throw new PasetoSignatureException("Signature could not be validated in paseto token.");
        }

        Map<String, Object> claims = deserializer.deserialize(message);
        return new DefaultPaseto(Version.V1, Purpose.PUBLIC, new DefaultClaims(claims), footer);
    }

    private Paseto v1Local(byte[] encryptedBytes, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        SecretKey sharedSecret = keyResolver.resolveSharedKey(Version.V1, Purpose.LOCAL, footer);
        Assert.notNull(sharedSecret, "A shared secret could not be resolved.  A shared secret must be configured in " +
                "'Pasetos.parserBuilder().setSharedSecret(...)' or Pasetos.parserBuilder().setKeyResolver(...)");
        byte[] nonce = Arrays.copyOf(encryptedBytes, 32);
        byte[] payload = v1LocalCryptoProvider.decrypt(encryptedBytes, footerBytes, nonce, sharedSecret);
        Map<String, Object> claims = deserializer.deserialize(payload);
        return new DefaultPaseto(Version.V1, Purpose.LOCAL, new DefaultClaims(claims), footer);
    }

    private Paseto v2Public(byte[] payload, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        byte[] message = Arrays.copyOf(payload, payload.length - 64);
        byte[] signature = Arrays.copyOfRange(payload, payload.length - 64, payload.length);

        PublicKey publicKey = keyResolver.resolvePublicKey(Version.V2, Purpose.PUBLIC, footer);
        Assert.notNull(publicKey, "A public key could not be resolved.  A public key must be configured in " +
                "'Pasetos.parserBuilder().setPublicKey(...)' or Pasetos.parserBuilder().setKeyResolver(...)");
        boolean valid = v2PublicCryptoProvider.verify(message, footerBytes, signature, publicKey);
        if (!valid) {
            throw new PasetoSignatureException("Signature could not be validated in paseto token.");
        }

        Map<String, Object> claims = deserializer.deserialize(message);
        return new DefaultPaseto(Version.V2, Purpose.PUBLIC, new DefaultClaims(claims), footer);
    }

    private FooterClaims toFooter(byte[] footerBytes) {
        if (footerBytes.length != 0) {
            if (footerBytes[0] == '{' && footerBytes[footerBytes.length - 1] == '}') { // assume JSON
                return new DefaultFooterClaims(deserializer.deserialize(footerBytes));
            } else {
                return  new DefaultFooterClaims(new String(footerBytes, StandardCharsets.UTF_8));
            }
        } else {
            return  new DefaultFooterClaims("");
        }
    }

    /**
     * The current paseto spec registers the 'nbf' claim but does NOT provide validation information.  This library
     * uses the JWT spec for expiration:
     * https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-30#section-4.1.5
     *
     * Token MUST NOT be accepted on or after any specified exp time
     *
     * @param paseto
     */
    private void verifyNotBefore(Paseto paseto) {
        Instant now = clock.instant();
        Instant nbf = paseto.getClaims().getNotBefore();
        if (nbf != null) {

            Instant min = now.plus(allowedClockSkew);
            if (min.isBefore(nbf)) {
                String nbfVal = DateFormats.formatIso8601(nbf);
                String nowVal = DateFormats.formatIso8601(now);

                Duration diff = Duration.between(nbf, min);

                String msg = "JWT must not be accepted before " + nbfVal + ". Current time: " + nowVal +
                    ", a difference of " + diff + ".  Allowed clock skew: " +
                    this.allowedClockSkew + ".";
                throw new PrematurePasetoException(paseto, msg);
            }
        }
    }

    /**
     * The current paseto spec registers the 'exp' claim but does NOT provide validation information.  This library
     * uses the JWT spec for expiration:
     * https://tools.ietf.org/html/draft-ietf-oauth-json-web-token-30#section-4.1.4
     *
     * Token MUST NOT be accepted on or after any specified exp time
     *
     * @param paseto
     */
    private void verifyExpiration(Paseto paseto) {
        Instant now = clock.instant();
        Instant exp = paseto.getClaims().getExpiration();

        if (exp != null) {

            Instant max = now.minus(allowedClockSkew);
            if (max.isAfter(exp)) {
                String expVal = DateFormats.formatIso8601(exp);
                String nowVal = DateFormats.formatIso8601(now);

                Duration diff = Duration.between(max, exp);

                String msg = "Paseto expired at " + expVal + ". Current time: " + nowVal + ", a difference of " +
                        diff + ".  Allowed clock skew: " +
                        allowedClockSkew + ".";
                throw new ExpiredPasetoException(paseto, msg);
            }
        }
    }

    private void validateExpectedClaims(Paseto paseto) {
        validateExpected(paseto, paseto.getClaims(), userExpectedClaimsMap);
    }

    private void validateExpectedFooterClaims(Paseto paseto) {
        validateExpected(paseto, paseto.getFooter(), userExpectedFooterClaimsMap);
    }

        private void validateExpected(Paseto paseto, Map<String, Object> claims, Map<String, Predicate<Object>> expectedClaims) {
        expectedClaims.forEach((claimName, predicate) -> {

            Object actualClaimValue = normalize(claims.get(claimName));

            InvalidClaimException invalidClaimException = null;

            String description = "<unnamed predicate>";
            if (predicate instanceof DescribedPredicate) {
                description = ((DescribedPredicate<Object>) predicate).getDescription();
            }

            if (actualClaimValue == null) {

                String msg = String.format(ClaimPasetoException.MISSING_EXPECTED_CLAIM_MESSAGE_TEMPLATE, claimName, description);
                invalidClaimException = new MissingClaimException(paseto, claimName, description, msg);

            } else if (!predicate.test(actualClaimValue)) {

                String msg = String.format(ClaimPasetoException.INCORRECT_EXPECTED_CLAIM_MESSAGE_TEMPLATE,
                    claimName, description, actualClaimValue);

                invalidClaimException = new IncorrectClaimException(paseto, claimName, description, msg);
            }

            if (invalidClaimException != null) {
                throw invalidClaimException;
            }
        });
    }

    private static Object normalize(Object o) {
        if (o instanceof Integer) {
            o = ((Integer) o).longValue();
        }
        return o;
    }
}
