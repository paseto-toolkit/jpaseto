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
package dev.paseto.jpaseto.lang;

import dev.paseto.jpaseto.PasetoKeyException;
import dev.paseto.jpaseto.Version;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public final class Keys {

    private Keys() {}

    public static SecretKey secretKey(final byte[] bytes) {
        return new SecretKeySpec(bytes, "none");
    }

    public static SecretKey secretKey() {
        byte[] keyBytes = new byte[64];
        try {
            SecureRandom.getInstanceStrong().nextBytes(keyBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("JVM does not provide a strong secure random number generator", e);
        }
        return new SecretKeySpec(keyBytes, "none");
    }

    public static KeyPair keyPairFor(Version version) {
        if (Version.V1 == version) {
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("RSA");
                keyPairGenerator.initialize(2048);
                return keyPairGenerator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                throw new PasetoKeyException("Failed to generate RSA key pair", e);
            }
        } else if (Version.V2 == version) {
            try {
                KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance("Ed25519");
                return keyPairGenerator.generateKeyPair();
            } catch (NoSuchAlgorithmException e) {
                throw new PasetoKeyException("Failed to generate Ed25519 key pair", e);
            }
        }
        throw new PasetoKeyException("Failed to generate keypair, version is not supported: "+ version);
    }
}