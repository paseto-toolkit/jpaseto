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

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public final class Hmacs {
    private Hmacs() {}

    public static byte[] hmacSha384(byte[] key, byte[] input) {
        try {
            Mac mac = Mac.getInstance("HmacSHA384"); //""HMac-SHA384"
            SecretKeySpec secretKeySpec = new SecretKeySpec(key, "HmacSHA384"); //"HMac-SHA384"
            mac.init(secretKeySpec);
            return mac.doFinal(input);
        } catch (NoSuchAlgorithmException | InvalidKeyException e) {
            throw new SecurityException("Could not calculate 'HmacSHA384'", e);
        }
    }

    static byte[] createNonce(byte[] key, byte[] input) {
        return Arrays.copyOf(Hmacs.hmacSha384(key, input), 32);
    }
}
