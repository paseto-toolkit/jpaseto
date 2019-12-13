package dev.paseto.jpaseto.impl.crypto;

import java.security.PrivateKey;
import java.security.PublicKey;

public interface V2PublicCryptoProvider {

    byte[] sign(byte[] payload, byte[] footer, PrivateKey privateKey);

    boolean verify(byte[] message, byte[] footer, byte[] signature, PublicKey publicKey);
}
