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

import dev.paseto.jpaseto.Claims;

import java.time.Instant;
import java.util.Date;
import java.util.Map;

public class DefaultClaims extends ClaimsMap implements Claims {

    public DefaultClaims(Map<String, Object> claims) {
        super(claims);
    }

    @Override
    public Object put(String key, Object value) {
        if (isSpecDate(key)) {
            return super.put(key, toInstant(value, key));
        }
        return super.put(key, value);
    }

    @Override
    public <T> T get(String claimName, Class<T> requiredType) {
        Object value = get(claimName);
        if (value == null) {
            return null;
        }

        if (Instant.class.equals(requiredType)) {
            if (isSpecDate(claimName)) {
                value = toSpecDate(value, claimName);
            } else {
                value = toInstant(value, claimName);
            }
        }

        if (Date.class.equals(requiredType)) {
            if (isSpecDate(claimName)) {
                value = Date.from(toSpecDate(value, claimName));
            } else {
                value = Date.from(toInstant(value, claimName));
            }
        }

        return castClaimValue(value, requiredType);
    }

    @Override
    protected boolean isSpecDate(String claimName) {
        return Claims.EXPIRATION.equals(claimName) ||
            Claims.ISSUED_AT.equals(claimName) ||
            Claims.NOT_BEFORE.equals(claimName);
    }
}
