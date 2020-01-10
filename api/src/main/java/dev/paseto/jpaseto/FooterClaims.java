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

/**
 * A paseto footer. Paseto footers per spec can either be a String or a JSON block. To retrieve the footer value as a
 * String use {@link FooterClaims#value()}.
 * @since 0.1.0
 */
public interface FooterClaims extends Map<String, Object> {

    /** Paseto {@code Key ID} claims parameter name: <code>"kid"</code>, registered footer claim. */
    String KEY_ID = "kid";

    <T> T get(String claimName, Class<T> requiredType);

    default String getKeyId() {
        return get(KEY_ID, String.class);
    }

    String value();
}
