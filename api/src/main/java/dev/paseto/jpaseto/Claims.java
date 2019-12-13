package dev.paseto.jpaseto;

import java.time.Instant;
import java.util.Map;

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
