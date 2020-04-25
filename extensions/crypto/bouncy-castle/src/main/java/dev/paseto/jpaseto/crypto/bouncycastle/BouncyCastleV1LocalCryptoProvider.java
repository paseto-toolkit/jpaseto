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
package dev.paseto.jpaseto.crypto.bouncycastle;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.impl.crypto.BaseV1LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;
import org.bouncycastle.crypto.generators.HKDFBytesGenerator;
import org.bouncycastle.crypto.params.HKDFParameters;
import org.bouncycastle.crypto.util.DigestFactory;

import javax.crypto.SecretKey;

@AutoService(V1LocalCryptoProvider.class)
public class BouncyCastleV1LocalCryptoProvider extends BaseV1LocalCryptoProvider {

    @Override
    protected byte[] hkdfSha384(SecretKey sharedSecret, byte[] salt, byte[] info) {
        HKDFBytesGenerator hkdfBytesGenerator = new HKDFBytesGenerator(DigestFactory.createSHA384());
        hkdfBytesGenerator.init(new HKDFParameters(sharedSecret.getEncoded(), salt, info));
        byte[] result = new byte[32];
        hkdfBytesGenerator.generateBytes(result, 0, result.length);
        return result;
    }
}
