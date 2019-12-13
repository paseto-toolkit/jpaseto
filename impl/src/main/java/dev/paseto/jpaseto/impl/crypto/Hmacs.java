package dev.paseto.jpaseto.impl.crypto;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class Hmacs {
    private Hmacs() {}

    public static byte[] hmacSha384(byte[] key, byte[] input) {
        try {
            Mac mac = Mac.getInstance("HmacSHA384"); //""HMac-SHA384"
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA384"); //"HMac-SHA384"
            mac.init(secretKeySpec);
            return mac.doFinal(input);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SecurityException("Could not calculate 'HmacSHA384'", e);
        }
    }

    static byte[] createNonce(byte[] key, byte[] input) {
        return Arrays.copyOf(Hmacs.hmacSha384(key, input), 32);
    }
}
