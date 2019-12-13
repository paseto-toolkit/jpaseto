/*
 * Copyright (C) 2014 jsonwebtoken.io
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

import javax.crypto.SecretKey;
import java.security.PublicKey;

/**
 * A {@code PubicKeyResolver} can be used by a {@link PasetoParser} to find a public key that
 * should be used to verify a paseto token signature.
 *
 * <p>A {@code PubicKeyResolver} is necessary when the signing key is not already known before parsing the paseto token and the
 * token payload or footer (plaintext or claims) must be inspected first to determine how to look up the signing key.
 * Once returned by the resolver, the PasetoParser will then verify the token signature with the returned key.  For
 * example:</p>
 *
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
 *
 * <p>A {@code PubicKeyResolver} is invoked once during parsing before the signature is verified.</p>
 *
 * <h3>KeyResolverAdapter</h3>
 *
 * <p>If you only need to resolve a signing key for a particular type of token, consider using
 * the {@link KeyResolverAdapter} and overriding only the method you need to support instead of
 * implementing this interface directly.</p>
 *
 * @see KeyResolverAdapter
 * @since 0.1
 */
public interface KeyResolver {

    /**
     * Returns the signing key that should be used to validate a digital signature for the paseto token.
     *
     * @param version the version of the token to be parsed
     * @param purpose the purpose of the token to be parsed
     * @param footer the footer containing claims or plain text of the token to be parsed
     * @return the public key that should be used to validate a digital signature for the token.
     */
    PublicKey resolvePublicKey(Version version, Purpose purpose, FooterClaims footer);

    /**
     * Returns the signing key that should be used to validate a digital signature for the paseto token.
     *
     * @param version the version of the token to be parsed
     * @param purpose the purpose of the token to be parsed
     * @param footer the footer containing claims or plain text of the token to be parsed
     * @return the shared key that should be used to decrypt the token.
     */
    SecretKey resolveSharedKey(Version version, Purpose purpose, FooterClaims footer);
}
