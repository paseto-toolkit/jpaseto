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
