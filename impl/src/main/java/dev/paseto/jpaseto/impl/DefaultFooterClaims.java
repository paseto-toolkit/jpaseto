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

import dev.paseto.jpaseto.FooterClaims;

import java.util.Collections;
import java.util.Map;

public class DefaultFooterClaims extends ClaimsMap implements FooterClaims {

    private final String value;

    DefaultFooterClaims(Map<String, Object> claims) {
        this(claims, dev.paseto.jpaseto.lang.Collections.isEmpty(claims) ? "" : null);
    }

    DefaultFooterClaims(String value) {
        this(null, value);
    }

    private DefaultFooterClaims(Map<String, Object> claims, String value) {
        super(claims != null
                ? Collections.unmodifiableMap(claims)
                : Collections.emptyMap());
        this.value = value;
    }

    @Override
    public <T> T get(String claimName, Class<T> requiredType) {
        return super.get(claimName, requiredType);
    }

    @Override
    public String value() {
        return value;
    }
}