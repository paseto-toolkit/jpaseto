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

import java.time.Instant;
import java.util.Map;

/**
 * A map representing a the body of a paseto token. Along with get arbitrary values using the {@link Map} interface,
 * methods to access registered claims.
 * @see #get(String, Class)
 * @since 0.1.0
 */
public interface Claims extends Map<String, Object> {

    /** Paseto {@code Issuer} claims parameter name: <code>"iss"</code>. */
    String ISSUER = "iss";

    /** Paseto {@code Subject} claims parameter name: <code>"sub"</code>. */
    String SUBJECT = "sub";

    /** Paseto {@code Audience} claims parameter name: <code>"aud"</code>. */
    String AUDIENCE = "aud";

    /** Paseto {@code Expiration} claims parameter name: <code>"exp"</code>. */
    String EXPIRATION = "exp";

    /** Paseto {@code Not Before} claims parameter name: <code>"nbf"</code>. */
    String NOT_BEFORE = "nbf";

    /** Paseto {@code Issued At} claims parameter name: <code>"iat"</code>. */
    String ISSUED_AT = "iat";

    /** Paseto {@code Token ID} claims parameter name: <code>"jti"</code>. */
    String TOKEN_ID = "jti";

    <T> T get(String claimName, Class<T> requiredType);

    default String getIssuer() {
        return get(ISSUER, String.class);
    }

    default String getSubject() {
        return get(SUBJECT, String.class);
    }

    default String getAudience() {
        return get(AUDIENCE, String.class);
    }

    default Instant getExpiration() {
        return get(EXPIRATION, Instant.class);
    }

    default Instant getNotBefore() {
        return get(NOT_BEFORE, Instant.class);
    }

    default Instant getIssuedAt() {
        return get(ISSUED_AT, Instant.class);
    }

    default String getTokenId() {
        return get(TOKEN_ID, String.class);
    }
}
