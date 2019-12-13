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

@SuppressWarnings("unchecked")
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