package dev.paseto.jpaseto.crypto.sodium;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.impl.crypto.PreAuthEncoder;
import dev.paseto.jpaseto.impl.crypto.V2PublicCryptoProvider;
import org.apache.tuweni.crypto.sodium.Signature;

import java.nio.charset.StandardCharsets;
import java.security.PrivateKey;
import java.security.PublicKey;

@AutoService(V2PublicCryptoProvider.class)
public class SodiumV2PublicCryptoProvider implements V2PublicCryptoProvider {

    private static final byte[] HEADER_BYTES = "v2.public.".getBytes(StandardCharsets.UTF_8);

    @Override
    public byte[] sign(byte[] payload, byte[] footer, PrivateKey privateKey) {
        byte[] m2 = PreAuthEncoder.encode(HEADER_BYTES, payload, footer);
        return Signature.signDetached(m2, Signature.SecretKey.fromBytes(privateKey.getEncoded()));
    }

    @Override
    public boolean verify(byte[] message, byte[] footer, byte[] signature, PublicKey publicKey) {
        byte[] m2 = PreAuthEncoder.encode(HEADER_BYTES, message, footer);
        return Signature.verifyDetached(m2, signature, Signature.PublicKey.fromBytes(publicKey.getEncoded()));
    }
}
