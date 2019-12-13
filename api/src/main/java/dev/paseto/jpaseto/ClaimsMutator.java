package dev.paseto.jpaseto;

import java.time.Instant;

interface ClaimsMutator<T extends ClaimsMutator> {

    T claim(String key, Object value);

    T footerClaim(String key, Object value);

    T setFooter(String footer);

    default T setIssuer(String iss) {
        claim(Claims.ISSUER, iss);
        return (T) this;
    }

    default T setSubject(String sub) {
        claim(Claims.SUBJECT, sub);
        return (T) this;
    }

    default T setAudience(String aud) {
        claim(Claims.AUDIENCE, aud);
        return (T) this;
    }

    default T setExpiration(Instant exp) {
        claim(Claims.EXPIRATION, exp);
        return (T) this;
    }

    default T setNotBefore(Instant nbf) {
        claim(Claims.NOT_BEFORE, nbf);
        return (T) this;
    }

    default T setIssuedAt(Instant iat) {
        claim(Claims.ISSUED_AT, iat);
        return (T) this;
    }

    default T setTokenId(String jti) {
        claim(Claims.TOKEN_ID, jti);
        return (T) this;
    }

    default T setKeyId(String kid) {
        footerClaim(FooterClaims.KEY_ID, kid);
        return (T) this;
    }
}