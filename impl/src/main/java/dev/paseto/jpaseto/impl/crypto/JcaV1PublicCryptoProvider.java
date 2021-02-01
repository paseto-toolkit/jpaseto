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
package dev.paseto.jpaseto.impl.crypto;

import dev.paseto.jpaseto.PasetoSignatureException;

import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;
import java.security.spec.MGF1ParameterSpec;
import java.security.spec.PSSParameterSpec;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JcaV1PublicCryptoProvider implements V1PublicCryptoProvider {

    private static final boolean IS_IN_BC_FIPS_MODE = Boolean.getBoolean("org.bouncycastle.fips.approved_only");

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
        Signature rsaSignature;
        if (IS_IN_BC_FIPS_MODE) {
            // bouncy castle's FIPS-approved mode does not directly expose RSASSA-PSS, so we must construct an RSA
            // signature using the correct parameters. RSASSA-PSS and SHA384withRSAandMGF1 can be used interchangeably,
            // but SHA384withRSAandMGF1 must be used in the BC-FIPS approved mode.
            try {
                rsaSignature = Signature.getInstance("SHA384withRSAandMGF1", "BCFIPS");
                rsaSignature.setParameter(new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
                return rsaSignature;
            } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new PasetoSignatureException("Could not load signature algorithm 'SHA384withRSAandMGF1' ensure you are using bc-fips.jar", e);
            }
        } else {
            // In normal operation we can just use whatever provider happens to be loaded such that it exposes the RSASSA-PSS algorithm
            try {
                rsaSignature = Signature.getInstance("RSASSA-PSS");
                rsaSignature.setParameter(new PSSParameterSpec("SHA-384", "MGF1", new MGF1ParameterSpec("SHA-384"), 48, 1));
                return rsaSignature;
            } catch (NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
                throw new PasetoSignatureException("Could not load signature algorithm 'RSASSA-PSS' ensure you are using jpaseto-bouncy-castle.jar or Java 11+", e);
            }
        }
    }
}