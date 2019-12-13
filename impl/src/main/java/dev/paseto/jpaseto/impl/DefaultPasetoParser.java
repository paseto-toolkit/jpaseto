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
import dev.paseto.jpaseto.io.Deserializer;
import dev.paseto.jpaseto.lang.Assert;
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

    private final KeyResolver keyResolver;
    private final Deserializer<Map<String, Object>> deserializer;
    private final Clock clock;
    private final Duration allowedClockSkewMillis;
    private final Map<String, Predicate<Object>> userExpectedClaimsMap;

    DefaultPasetoParser(KeyResolver keyResolver, Deserializer<Map<String, Object>> deserializer, Clock clock, Duration allowedClockSkewMillis, Map<String, Predicate<Object>> expectedClaimsMap) {
        this.keyResolver = keyResolver;
        this.deserializer = deserializer;
        this.clock = clock;
        this.allowedClockSkewMillis = allowedClockSkewMillis;
        this.userExpectedClaimsMap = Collections.unmodifiableMap(expectedClaimsMap);
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
            throw new UnsupportedPasetoException("Paseto token with header: '" + version.toString() + "." + purpose.toString() +".' is not supported.");
        }

        verifyExpiration(paseto);
        verifyNotBefore(paseto);
        validateExpectedClaims(paseto);

        return paseto;
    }

    private Paseto v2Local(byte[] encryptedBytes, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        SecretKey sharedSecret = keyResolver.resolveSharedKey(Version.V2, Purpose.LOCAL, footer);
        byte[] payload = CryptoProviders.v2LocalCryptoProvider().decrypt(encryptedBytes, footerBytes, sharedSecret);
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
        boolean valid = CryptoProviders.v1PublicCryptoProvider().verify(message, footerBytes, signature, publicKey);
        if (!valid) {
            throw new PasetoSignatureException("Signature could not be validated in paseto token.");
        }

        Map<String, Object> claims = deserializer.deserialize(message);
        return new DefaultPaseto(Version.V1, Purpose.PUBLIC, new DefaultClaims(claims), footer);
    }

    private Paseto v1Local(byte[] encryptedBytes, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        SecretKey sharedSecret = keyResolver.resolveSharedKey(Version.V2, Purpose.LOCAL, footer);
        byte[] nonce = Arrays.copyOf(encryptedBytes, 32);
        byte[] payload = CryptoProviders.v1LocalCryptoProvider().decrypt(encryptedBytes, footerBytes, nonce, sharedSecret);
        Map<String, Object> claims = deserializer.deserialize(payload);
        return new DefaultPaseto(Version.V1, Purpose.LOCAL, new DefaultClaims(claims), footer);
    }

    private Paseto v2Public(byte[] payload, byte[] footerBytes) {
        // parse footer to map (if available)
        FooterClaims footer = toFooter(footerBytes);

        byte[] message = Arrays.copyOf(payload, payload.length - 64);
        byte[] signature = Arrays.copyOfRange(payload, payload.length - 64, payload.length);

        PublicKey publicKey = keyResolver.resolvePublicKey(Version.V1, Purpose.PUBLIC, footer);
        boolean valid = CryptoProviders.v2PublicCryptoProvider().verify(message, footerBytes, signature, publicKey);
        if (!valid) {
            throw new PasetoSignatureException("Signature could not be validated in paseto token.");
        }

        Map<String, Object> claims = deserializer.deserialize(message);
        return new DefaultPaseto(Version.V2, Purpose.PUBLIC, new DefaultClaims(claims), footer);
    }

    private FooterClaims toFooter(byte[] footerBytes) {
        if (footerBytes.length != 0) {
            if (footerBytes[0] == '{') {
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

            Instant min = now.plus(allowedClockSkewMillis);
            if (min.isBefore(nbf)) {
                String nbfVal = DateFormats.formatIso8601(nbf);
                String nowVal = DateFormats.formatIso8601(now);

                Duration diff = Duration.between(nbf, min);

                String msg = "JWT must not be accepted before " + nbfVal + ". Current time: " + nowVal +
                    ", a difference of " + diff + ".  Allowed clock skew: " +
                    this.allowedClockSkewMillis + ".";
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

            Instant max = now.minus(allowedClockSkewMillis);
            if (max.isAfter(exp)) {
                String expVal = DateFormats.formatIso8601(exp);
                String nowVal = DateFormats.formatIso8601(now);

                Duration diff = Duration.between(max, exp);

                String msg = "Paseto expired at " + expVal + ". Current time: " + nowVal + ", a difference of " +
                        diff + ".  Allowed clock skew: " +
                        allowedClockSkewMillis + ".";
                throw new ExpiredPasetoException(paseto, msg);
            }
        }
    }

    private void validateExpectedClaims(Paseto paseto) {
        userExpectedClaimsMap.forEach((claimName, predicate) -> {

            Object actualClaimValue = normalize(paseto.getClaims().get(paseto));

            InvalidClaimException invalidClaimException = null;

            String description = "<unnamed predicate>";
            if (predicate instanceof DescribedPredicate) {
                description = ((DescribedPredicate<Object>) predicate).getDescription();
            }

            if (actualClaimValue == null) {

                String msg = String.format(ClaimPasetoException.MISSING_EXPECTED_CLAIM_MESSAGE_TEMPLATE, claimName, description);
                invalidClaimException = new MissingClaimException(paseto, msg);

            } else if (!predicate.test(actualClaimValue)) {

                String msg = String.format(ClaimPasetoException.INCORRECT_EXPECTED_CLAIM_MESSAGE_TEMPLATE,
                    claimName, description, actualClaimValue);

                invalidClaimException = new IncorrectClaimException(paseto, msg);
            }

            if (invalidClaimException != null) {
                invalidClaimException.setClaimName(claimName);
                invalidClaimException.setClaimDescription(description);
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
