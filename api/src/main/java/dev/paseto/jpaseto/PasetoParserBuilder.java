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
package dev.paseto.jpaseto;

import dev.paseto.jpaseto.io.Deserializer;
import dev.paseto.jpaseto.lang.DescribedPredicate;
import dev.paseto.jpaseto.lang.Keys;

import javax.crypto.SecretKey;
import java.security.PublicKey;
import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.function.Predicate;

public interface PasetoParserBuilder {

    /**
     * Sets the {@link KeyResolver} used to acquire the <code>signing key</code> that should be used to verify
     * a paseto tokens's signature.
     *
     * <p>Specifying a {@code SigningKeyResolver} is necessary when the signing key is not already known before parsing
     * the token and the footer must be inspected first to determine how to
     * look up the signing key.  Once returned by the resolver, the PasetoParser will then verify the paseto token's
     * signature with the returned key.  For example:
     * <p>
     * <pre>
     * Paseto token = Pasetos.parserBuilder().setSigningKeyResolver(new KeyResolverAdapter() {
     *         &#64;Override
     *         public byte[] resolvePublicKeyBytes(Paseto paseto) {
     *             //inspect the header or claims, lookup and return the signing key
     *             return getPublicKeyBytes(paseto); //implement me
     *         }})
     *     .build()
     *     .parse(tokenString);
     * </pre>
     * <p>
     * <p>A {@code SigningKeyResolver} is invoked once during parsing before the signature is verified.</p>
     * <p>
     * <p>This method should only be used if a signing key is not provided by the other {@code setSigningKey*} builder
     * methods.</p>
     *
     * To construct a PasetoParser use the corresponding builder via {@link Pasetos#parserBuilder()}. This will construct an
     * immutable {@link PasetoParser}.
     *
     * @param keyResolver the signing key resolver used to retrieve the signing key.
     * @return the parser builder for method chaining.
     */
    PasetoParserBuilder setKeyResolver(KeyResolver keyResolver);

    PasetoParserBuilder setPublicKey(PublicKey publicKey);

    default PasetoParserBuilder setSharedSecret(byte[] sharedSecret) {
        setSharedSecret(Keys.secretKey(sharedSecret));
        return this;
    }

    PasetoParserBuilder setSharedSecret(SecretKey sharedSecret);

    PasetoParserBuilder setDeserializer(Deserializer<Map<String, Object>> deserializer);

    PasetoParser build();

    /**
     * Ensures that the specified {@code iss} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param iss expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireIssuer(String iss) {
        return require(Claims.ISSUER, DescribedPredicate.equalTo(iss));
    }

    /**
     * Ensures that the specified {@code sub} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param sub expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireSubject(String sub) {
        return require(Claims.SUBJECT, DescribedPredicate.equalTo(sub));
    }

    /**
     * Ensures that the specified {@code aud} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param aud expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireAudience(String aud) {
        return require(Claims.AUDIENCE, DescribedPredicate.equalTo(aud));
    }

    /**
     * Ensures that the specified {@code exp} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param exp expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireExpiration(Instant exp) {
        return require(Claims.EXPIRATION, DescribedPredicate.equalTo(exp));
    }

    /**
     * Ensures that the specified {@code nbf} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param nbf expected claim value
     * @return the parser builder for method chaining
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireNotBefore(Instant nbf) {
        return require(Claims.NOT_BEFORE, DescribedPredicate.equalTo(nbf));
    }

    /**
     * Ensures that the specified {@code iat} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param iat expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireIssuedAt(Instant iat) {
        return require(Claims.ISSUED_AT, DescribedPredicate.equalTo(iat));
    }

    /**
     * Ensures that the specified {@code jti} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param jti expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireTokenId(String jti) {
        return require(Claims.TOKEN_ID, DescribedPredicate.equalTo(jti));
    }

    /**
     * Ensures that the specified {@code kid} exists in the parsed Paseto footer.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param kid expected claim value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder requireKeyId(String kid) {
        return requireFooter(FooterClaims.KEY_ID, DescribedPredicate.equalTo(kid));
    }

    /**
     * Ensures that the specified {@code claimName} exists in the parsed Paseto.  If missing or if the parsed
     * value does not equal the specified value, an exception will be thrown indicating that the
     * Paseto is invalid and may not be used.
     *
     * @param claimName
     * @param value
     * @return the parser builder for method chaining.
     * @see MissingClaimException
     * @see IncorrectClaimException
     */
    default PasetoParserBuilder require(String claimName, Object value) {
        return require(claimName, DescribedPredicate.equalTo(value));
    }

    PasetoParserBuilder require(String claimName, Predicate<Object> value);

    PasetoParserBuilder requireFooter(String claimName, Predicate<Object> value);

    /**
     * Sets the {@link Clock} that determines the timestamp to use when validating the parsed Paseto.
     * The parser uses a default Clock implementation that simply returns {@code new Date()} when called.
     *
     * @param clock a {@code Clock} object to return the timestamp to use when validating the parsed Paseto.
     * @return the parser builder for method chaining.
     */
    PasetoParserBuilder setClock(Clock clock);

    /**
     * Sets the amount of clock skew tolerate when verifying the local time against the {@code exp}
     * and {@code nbf} claims.
     *
     * @param allowedClockSkew the duration to tolerate for clock skew when verifying {@code exp} or {@code nbf} claims.
     * @return the parser builder for method chaining.
     */
    PasetoParserBuilder setAllowedClockSkew(Duration allowedClockSkew);
}
