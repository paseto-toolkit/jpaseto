package dev.paseto.jpaseto.impl.crypto;

import dev.paseto.jpaseto.PasetoSignatureException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JcaV1PublicCryptoProvider implements V1PublicCryptoProvider {

    private static final byte[] HEADER_BYTES = "v1.public.".getBytes(UTF_8);

    @Override
    public byte[] sign(byte[] payload, byte[] footer, PrivateKey privateKey) {

        // 2
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, payload, footer);

        // 3
        try {
            Signature rsaSignature = pssSignature();
            rsaSignature.initSign(privateKey);
            rsaSignature.update(preAuth);
            return rsaSignature.sign();
        } catch (InvalidKeyException | SignatureException e) {
            throw new PasetoSignatureException("Failed to sign token", e);
        }
    }

    @Override
    public boolean verify(byte[] message, byte[] footer, byte[] signature, PublicKey publicKey) {

        // 4
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, message, footer);

        try {
            Signature rsaSignature = pssSignature();
            rsaSignature.initVerify(publicKey);
            rsaSignature.update(preAuth);
            return rsaSignature.verify(signature);
        } catch (InvalidKeyException | SignatureException e) {
            throw new PasetoSignatureException("Could not verify token signature", e);
        }
    }

    private Signature pssSignature() {
        try {
            Signature rsaSignature = Signature.getInstance("RSASSA-PSS");
            rsaSignature.setParameter(new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
            return rsaSignature;
        } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new PasetoSignatureException("Could not load signature algorithm 'RSASSA-PSS' ensure you are using jpaseto-bouncy-castle.jar or Java 11+", e);
        }
    }
}