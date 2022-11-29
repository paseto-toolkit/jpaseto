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
package dev.paseto.jpaseto;

import java.util.Map;

import dev.paseto.jpaseto.io.Serializer;

/**
 * A builder interface for creating paseto tokens.
 * @see Pasetos
 * @since 0.1
 * @param <T> A child implementation of PasetoBuilder
 */
public interface PasetoTokenBuilder<T extends PasetoTokenBuilder> extends ClaimsMutator<T> {
    T setSerializer(Serializer<Map<String, Object>> serializer);

    byte[] payloadAsBytes();

    byte[] footerAsBytes();

    String noPadBase64(byte[]... inputs);

    String footerToString(byte[] footer);
}
