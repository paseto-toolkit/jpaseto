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

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.Signature;
import java.security.SignatureException;

public class JcaV2PublicCryptoProvider implements V2PublicCryptoProvider {

    private static final byte[] HEADER_BYTES = "v2.public.".getBytes(StandardCharsets.UTF_8);

    @Override
    public byte[] sign(byte[] payload, byte[] footer, PrivateKey privateKey) {
        // 2
        byte[] m2 = PreAuthEncoder.encode(HEADER_BYTES, payload, footer);

        // 3
        try {
            Signature signature = signature();
            signature.initSign(privateKey);
            signature.update(m2);
            return signature.sign();
        } catch (InvalidKeyException | SignatureException e) {
            throw new PasetoSignatureException("Failed to sign token", e);
        }
    }

    @Override
    public boolean verify(byte[] message, byte[] footer, byte[] signature, PublicKey publicKey) {
        // 4
        byte[] preAuth = PreAuthEncoder.encode(HEADER_BYTES, message, footer);

        try {
            Signature eddsaSignature = signature();
            eddsaSignature.initVerify(publicKey);
            eddsaSignature.update(preAuth);
            return eddsaSignature.verify(signature);
        } catch (InvalidKeyException | SignatureException e) {
            throw new PasetoSignatureException("Could not verify token signature", e);
        }
    }

    private Signature signature() {
        try {
            return Signature.getInstance("Ed25519");
        } catch (NoSuchAlgorithmException e) {
            throw new PasetoSignatureException("Could not load signature algorithm 'Ed25519' ensure you are using jpaseto-bouncy-castle.jar or Java 11+", e);
        }
    }
}