/*
 * Copyright 2020-Present paseto.dev
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
package dev.paseto.jpaseto.crypto.hkdf;

import at.favre.lib.crypto.HKDF;
import at.favre.lib.crypto.HkdfMacFactory;
import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.impl.crypto.BaseV1LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;

import javax.crypto.SecretKey;

/**
 * @since 0.5.0
 */
@AutoService(V1LocalCryptoProvider.class)
public class HKDFV1LocalCryptoProvider extends BaseV1LocalCryptoProvider {

    @Override
    protected byte[] hkdfSha384(SecretKey sharedSecret, byte[] salt, byte[] info) {
        HKDF hkdfSha384 = HKDF.from(new HkdfMacFactory.Default("HmacSHA384"));
        return hkdfSha384.extractAndExpand(salt, sharedSecret.getEncoded(), info, 32);
    }
}
