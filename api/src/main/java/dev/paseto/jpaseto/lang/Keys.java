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

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.security.PrivateKey;
import java.security.PublicKey;

public final class Keys {

    public static PublicKey ed25519PublicKey(final byte[] bytes) {

        return new PublicKey() {
            @Override
            public String getAlgorithm() {
                return "Ed25519";
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return bytes;
            }
        };
    }

    public static PrivateKey ed25519PrivateKey(final byte[] bytes) {

        return new PrivateKey() {
            @Override
            public String getAlgorithm() {
                return "Ed25519";
            }

            @Override
            public String getFormat() {
                return null;
            }

            @Override
            public byte[] getEncoded() {
                return bytes;
            }
        };
    }

    public static SecretKey secretKey(final byte[] bytes) {
        return new SecretKeySpec(bytes, "none");
    }
}
