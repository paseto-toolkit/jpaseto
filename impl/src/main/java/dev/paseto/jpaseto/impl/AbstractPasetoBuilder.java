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
package dev.paseto.jpaseto.impl;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import dev.paseto.jpaseto.PasetoBuilder;
import dev.paseto.jpaseto.PasetoTokenBuilder;
import dev.paseto.jpaseto.impl.lang.Bytes;
import dev.paseto.jpaseto.io.Serializer;
import dev.paseto.jpaseto.lang.Collections;
import dev.paseto.jpaseto.lang.Services;
import dev.paseto.jpaseto.lang.Strings;

abstract class AbstractPasetoBuilder<T extends PasetoBuilder> implements PasetoBuilder<T> {
    private Serializer<Map<String, Object>> serializer;

    /**
     * Returns serializer
     * @return
     */
    public Serializer<Map<String, Object>> getSerializer() {
        // if null just return the first service
        if (serializer == null) {
            return Services.loadFirst(Serializer.class);
        }
        return serializer;
    }

    /**
     * Sets serializer and returns original instance of object
     * @return
     */
    public PasetoBuilder<T> setSerializer(Serializer<Map<String, Object>> newSerializer) {
        this.serializer = newSerializer;
        return this;
    }
}
