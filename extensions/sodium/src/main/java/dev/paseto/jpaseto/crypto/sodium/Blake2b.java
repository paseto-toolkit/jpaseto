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
package dev.paseto.jpaseto.crypto.sodium;

import org.apache.tuweni.crypto.sodium.Sodium;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

class Blake2b {

    private Blake2b() {}

    static byte[] hash(int hashLength, byte[] bytes, byte[] key) {

        // static int crypto_generichash_blake2b(byte[] out, long outlen, byte[] in, long inlen, byte[] key, long keylen)

        byte[] output = new byte[hashLength];

        try {
            Method blake2b = Sodium.class.getDeclaredMethod("crypto_generichash_blake2b",
                    byte[].class, long.class,
                    byte[].class, long.class,
                    byte[].class, long.class);

            blake2b.setAccessible(true);
            blake2b.invoke(null,
                           output, output.length,
                           bytes, bytes.length,
                           key, key.length);

            return output;

        } catch (NoSuchMethodException e) {
            throw new IllegalStateException("Failed to find method 'crypto_generichash_blake2b' in: " + Sodium.class, e);
        } catch (IllegalAccessException | InvocationTargetException e) {
            throw new IllegalStateException("Failed to calculate BLAKE2b digest", e);
        }
    }
}
