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
import dev.paseto.jpaseto.RequiredTypeException;

import java.text.ParseException;
import java.time.Instant;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

abstract class ClaimsMap implements Map<String, Object> {

   private static final String CONVERSION_ERROR_MSG = "Cannot convert existing claim value of type '%s' to desired type " +
        "'%s'. JPaseto only converts simple String, Instant, Date, Long, Integer, Short and Byte types automatically. " +
        "Anything more complex is expected to be already converted to your desired type by the JSON Deserializer " +
        "implementation. You may specify a custom Deserializer for a JwtParser with the desired conversion " +
        "configuration via the PasetoParserBuilder.setDeserializer() method. " +
        "See https://github.com/jwtk/jjwt#custom-json-processor for more information. If using Jackson, you can " +
        "specify custom claim POJO types as described in https://github.com/jwtk/jjwt#json-jackson-custom-types";

    // TODO: add link to message

    private final Map<String, Object> claims;

    public ClaimsMap(Map<String, Object> claims) {
        this.claims = claims;
    }

    @Override
    public int size() {
        return claims.size();
    }

    @Override
    public boolean isEmpty() {
        return claims.isEmpty();
    }

    @Override
    public boolean containsKey(Object key) {
        return claims.containsKey(key);
    }

    @Override
    public boolean containsValue(Object value) {
        return claims.containsValue(value);
    }

    @Override
    public Object get(Object key) {
        return claims.get(key);
    }

    @Override
    public Object put(String key, Object value) {
        return claims.put(key, value);
    }

    @Override
    public Object remove(Object key) {
        return claims.remove(key);
    }

    @Override
    public void putAll(Map<? extends String, ?> m) {
        claims.putAll(m);
    }

    @Override
    public void clear() {
        claims.clear();
    }

    @Override
    public Set<String> keySet() {
        return claims.keySet();
    }

    @Override
    public Collection<Object> values() {
        return claims.values();
    }

    @Override
    public Set<Entry<String, Object>> entrySet() {
        return claims.entrySet();
    }

    protected <T> T get(String claimName, Class<T> requiredType) {
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

    <T> T castClaimValue(Object value, Class<T> requiredType) {

        if (value instanceof Integer) {
            int intValue = (Integer) value;
            if (requiredType == Long.class) {
                value = (long) intValue;
            } else if (requiredType == Short.class && Short.MIN_VALUE <= intValue && intValue <= Short.MAX_VALUE) {
                value = (short) intValue;
            } else if (requiredType == Byte.class && Byte.MIN_VALUE <= intValue && intValue <= Byte.MAX_VALUE) {
                value = (byte) intValue;
            }
        }

        if (!requiredType.isInstance(value)) {
            throw new RequiredTypeException(String.format(CONVERSION_ERROR_MSG, value.getClass(), requiredType));
        }

        return requiredType.cast(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClaimsMap that = (ClaimsMap) o;
        return Objects.equals(claims, that.claims);
    }

    @Override
    public int hashCode() {
        return Objects.hash(claims);
    }

    protected static Instant toSpecDate(Object v, String name) {
        if (v == null) {
            return null;
            // TODO: not sure if this is valid, as the Paseto spec mandates ISO 8601
        } else if (v instanceof Number) {
            // https://github.com/jwtk/jjwt/issues/122:
            // The JWT RFC *mandates* NumericDate values are represented as seconds.
            // Because Because java.util.Date requires milliseconds, we need to multiply by 1000:
            long seconds = ((Number) v).longValue();
            v = seconds * 1000;
        } else if (v instanceof String) {
            // https://github.com/jwtk/jjwt/issues/122
            // The JWT RFC *mandates* NumericDate values are represented as seconds.
            // Because Because java.util.Date requires milliseconds, we need to multiply by 1000:
            try {
                long seconds = Long.parseLong((String) v);
                v = seconds * 1000;
            } catch (NumberFormatException ignored) {
            }
        }
        //v would have been normalized to milliseconds if it was a number value, so perform normal date conversion:
        return toInstant(v, name);
    }

    protected static Instant toInstant(Object v, String name) {
        if (v == null) {
            return null;
        } else if (v instanceof Date) {
            return ((Date) v).toInstant();
        } else if (v instanceof Calendar) { //since 0.10.0
            return ((Calendar) v).toInstant();
        } else if (v instanceof Number) {
            //assume millis:
            long millis = ((Number) v).longValue();
            return Instant.ofEpochMilli(millis);
        } else if (v instanceof String) {
            return parseIso8601Date((String) v, name); //ISO-8601 parsing since 0.10.0
        } else {
            throw new IllegalStateException("Cannot create Date from '" + name + "' value '" + v + "'.");
        }
    }

    private static Instant parseIso8601Date(String s, String name) throws IllegalArgumentException {
        try {
            return DateFormats.parseIso8601Date(s);
        } catch (ParseException e) {
            String msg = "'" + name + "' value does not appear to be ISO-8601-formatted: " + s;
            throw new IllegalArgumentException(msg, e);
        }
    }

    protected boolean isSpecDate(String claimName) {
        return false;
    }
}
