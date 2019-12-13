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

import dev.paseto.jpaseto.PasetoBuilder;
import dev.paseto.jpaseto.impl.lang.Bytes;
import dev.paseto.jpaseto.io.SerializationException;
import dev.paseto.jpaseto.io.Serializer;
import dev.paseto.jpaseto.lang.Collections;
import dev.paseto.jpaseto.lang.Services;
import dev.paseto.jpaseto.lang.Strings;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractPasetoBuilder<T extends PasetoBuilder> implements PasetoBuilder<T> {

    private final Map<String, Object> payload = new HashMap<>();
    private final Map<String, Object> footer = new HashMap<>();
    private String footerString = null;

    private Serializer<Map<String, ?>> serializer;

    @Override
    public T claim(String key, Object value) {
        payload.put(key, value);
        return (T) this;
    }

    @Override
    public T footerClaim(String key, Object value) {
        footer.put(key, value);
        return (T) this;
    }

    @Override
    public T setFooter(String footer) {
        this.footerString = footer;
        return (T) this;
    }


    protected Serializer<Map<String, ?>> getSerializer() {

        // if null just return the first service
        if (serializer == null) {
            return Services.loadFirst(Serializer.class);
        }
        return serializer;
    }

    @Override
    public T setSerializer(Serializer<Map<String, ?>> serializer) {
        this.serializer = serializer;
        return (T) this;
    }

    protected String footerToString(byte[] footer) {

        if (footer == null || footer.length == 0) {
            return "";
        }

        return "." + noPadBase64(footer);
    }

    protected String noPadBase64(byte[]... inputs) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(Bytes.concat(inputs));
    }

    protected byte[] payloadAsBytes() {
        try {
            return getSerializer().serialize(getPayload());
        } catch (SerializationException e) {
            throw new RuntimeException("Could not serialize Paseto payload", e);
        }
    }

    protected byte[] footerAsBytes() {

        if (Strings.hasText(getFooterString())) {
            return getFooterString().getBytes(StandardCharsets.UTF_8);
        }

        Map<String, Object> footer = getFooter();
        if (!Collections.isEmpty(footer)) {
            try {
                return getSerializer().serialize(footer);
            } catch (SerializationException e) {
                throw new RuntimeException("Could not serialize Paseto Footer", e);
            }
        }

        return new byte[0];
    }

    protected Map<String, Object> getPayload() {
        return payload;
    }

    protected Map<String, Object> getFooter() {
        return footer;
    }

    protected String getFooterString() {
        return footerString;
    }
}