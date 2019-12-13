package dev.paseto.jpaseto.impl.crypto;

import javax.crypto.SecretKey;

public interface V2LocalCryptoProvider {

    byte[] blake2b(byte[] random, byte[] payload);

    byte[] encrypt(byte[] payload, byte[] footer, byte[] nonce, SecretKey sharedSecret);

    byte[] decrypt(byte[] encryptedBytes, byte[] footer, SecretKey sharedSecret);
}
