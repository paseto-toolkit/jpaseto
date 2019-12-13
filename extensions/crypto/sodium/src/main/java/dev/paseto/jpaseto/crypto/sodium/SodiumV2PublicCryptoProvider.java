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
