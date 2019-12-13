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

import dev.paseto.jpaseto.lang.Assert;
import dev.paseto.jpaseto.lang.Keys;

import javax.crypto.SecretKey;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PublicKey;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.X509EncodedKeySpec;

/**
 * An <a href="http://en.wikipedia.org/wiki/Adapter_pattern">Adapter</a> implementation of the
 * {@link KeyResolver} interface that allows subclasses to process only the type of paseto token that
 * is known/expected for a particular case.
 *
 * <p>The {@link #resolvePublicKey(Version, Purpose, FooterClaims)} and
 * {@link #resolveSharedKey(Version, Purpose, FooterClaims)} method implementations delegate to the
 * {@link #resolvePublicKeyBytes(Version, Purpose, FooterClaims)} and
 * {@link #resolveSharedKeyBytes(Version, Purpose, FooterClaims)} methods respectively.  The latter two methods
 * simply throw exceptions:  they represent scenarios expected by calling code in known situations, and it is expected
 * that you override the implementation in those known situations; non-overridden *KeyBytes methods indicates that the
 * token input was unexpected.
 *
 * <p>If either {@link #resolvePublicKey(Version, Purpose, FooterClaims)} and {@link #resolveSharedKey(Version, Purpose, FooterClaims)}
 * are not overridden, one (or both) of the *KeyBytes variants must be overridden depending on your expected
 * use case.  You do not have to override any method that does not represent an expected condition.
 *
 * @since 0.1
 */
public class KeyResolverAdapter implements KeyResolver {

    @Override
    public PublicKey resolvePublicKey(Version version, Purpose purpose, FooterClaims footer) {

        Assert.isTrue(Purpose.PUBLIC == purpose,
                "Token purpose MUST be 'public' to resolve a public key, found: " + purpose);

        if (Version.V1 == version) {
            try {
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                return keyFactory.generatePublic(new X509EncodedKeySpec(resolvePublicKeyBytes(version, purpose, footer)));
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                throw new PasetoSignatureException("Failed to load RSA key.", e);
            }
        } else {
            return Keys.ed25519PublicKey(resolvePublicKeyBytes(version, purpose, footer));
        }
    }

    @Override
    public SecretKey resolveSharedKey(Version version, Purpose purpose, FooterClaims footer) {
        return Keys.secretKey(resolveSharedKeyBytes(version, purpose, footer));
    }

    /**
     * Convenience method invoked by {@link #resolvePublicKey(Version, Purpose, FooterClaims)} that obtains the
     * necessary public key bytes.  This implementation simply throws an exception: if the paseto token parsed
     * is 'public'.
     *
     * @param version the version of the token to be parsed
     * @param purpose the purpose of the token to be parsed
     * @param footer the footer containing claims or plain text of the token to be parsed
     * @return the signing key bytes to use to verify the paseto token signature.
     */
    protected byte[] resolvePublicKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
        throw new UnsupportedPasetoException("The specified KeyResolver implementation does not support " +
                                          "public key resolution.  Consider overriding either the " +
                                          "resolvePublicKeyBytes(Paseto) method.");
    }

    /**
     * Convenience method invoked by {@link #resolveSharedKey(Version, Purpose, FooterClaims)} that obtains the
     * necessary signing key bytes.  This implementation simply throws an exception: if the paseto token
     * parsed is 'local'.
     *
     * @param version the version of the token to be parsed
     * @param purpose the purpose of the token to be parsed
     * @param footer the footer containing claims or plain text of the token to be parsed
     * @return the shared key that should be used to decrypt the token.
     */
    protected byte[] resolveSharedKeyBytes(Version version, Purpose purpose, FooterClaims footer) {
        throw new UnsupportedPasetoException("The specified KeyResolver implementation does not support " +
                                          "shared secret key resolution.  Consider overriding either the " +
                                          "resolveSharedKeyBytes(Version, Purpose, FooterClaims) method.");
    }
}
