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
