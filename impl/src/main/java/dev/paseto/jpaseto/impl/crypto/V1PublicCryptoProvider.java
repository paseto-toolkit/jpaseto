package dev.paseto.jpaseto.impl.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface V1PublicCryptoProvider {

    default byte[] nonce(byte[] payload, byte[] randomBytes) {
        return Hmacs.createNonce(randomBytes, payload);
    }

    byte[] sign(byte[] payload, byte[] footer, PrivateKey privateKey);

    boolean verify(byte[] message, byte[] footer, byte[] signature, PublicKey publicKey);
}
