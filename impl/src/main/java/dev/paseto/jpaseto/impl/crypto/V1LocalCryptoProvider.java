package dev.paseto.jpaseto.impl.crypto;

import dev.paseto.jpaseto.PasetoKeyException;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public interface V1LocalCryptoProvider {

    byte[] encrypt(byte[] payload, byte[] footer, byte[] nonce, SecretKey sharedSecret);

    byte[] decrypt(byte[] encryptedBytes, byte[] footer, byte[] nonce, SecretKey sharedSecret);

    default byte[] nonce(byte[] payload, byte[] randomBytes) {
        return Hmacs.createNonce(randomBytes, payload);
    }

    static byte[] doCipher(int mode, byte[] key, byte[] nonce, byte[] input) {
        try {
            Cipher cipher = Cipher.getInstance("AES/CTR/NoPadding");
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "AES");
            cipher.init(mode, secretKeySpec, new IvParameterSpec(nonce));
            return cipher.doFinal(input);

        } catch (NoSuchAlgorithmException | NoSuchPaddingException | IllegalBlockSizeException | BadPaddingException | InvalidAlgorithmParameterException | InvalidKeyException e) {
            throw new SecurityException("Failed create cipher.");
        }
    }
}
