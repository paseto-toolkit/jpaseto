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

import dev.paseto.jpaseto.PasetoTokenBuilder;
import dev.paseto.jpaseto.impl.lang.Bytes;
import dev.paseto.jpaseto.io.Serializer;
import dev.paseto.jpaseto.lang.Collections;
import dev.paseto.jpaseto.lang.Services;
import dev.paseto.jpaseto.lang.Strings;

import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

abstract class AbstractPasetoTokenBuilder<T extends PasetoTokenBuilder> implements PasetoTokenBuilder<T> {

    private final Map<String, Object> payload = new HashMap<>();
    private final Map<String, Object> footer = new HashMap<>();
    private String footerString = null;

    private Serializer<Map<String, Object>> serializer;

    @Override
    public T claim(String key, Object value) {
        payload.put(key, value);
        return self();
    }

    @Override
    public T footerClaim(String key, Object value) {
        footer.put(key, value);
        return self();
    }

    @Override
    public T setFooter(String footer) {
        this.footerString = footer;
        return self();
    }

    @SuppressWarnings("unchecked")
    private T self() {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    protected Serializer<Map<String, Object>> getSerializer() {

        // if null just return the first service
        if (serializer == null) {
            return Services.loadFirst(Serializer.class);
        }
        return serializer;
    }

    @Override
    public T setSerializer(Serializer<Map<String, Object>> serializer) {
        this.serializer = serializer;
        return self();
    }

    public String footerToString(byte[] footer) {

        if (footer == null || footer.length == 0) {
            return "";
        }

        return "." + noPadBase64(footer);
    }

    public String noPadBase64(byte[]... inputs) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(Bytes.concat(inputs));
    }

    public byte[] payloadAsBytes() {
        return getSerializer().serialize(getPayload());
    }

    public byte[] footerAsBytes() {

        if (Strings.hasText(getFooterString())) {
            return getFooterString().getBytes(StandardCharsets.UTF_8);
        }

        Map<String, Object> tmpFooter = getFooter();
        if (!Collections.isEmpty(tmpFooter)) {
            return getSerializer().serialize(tmpFooter);
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
