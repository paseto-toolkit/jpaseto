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

/**
 * Enum representing Paseto purposes.
 * @since 0.1
 */
public enum Purpose {
    LOCAL("local"),
    PUBLIC("public");

    private final String name;

    Purpose(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Purpose from(String name) {
        for (Purpose purpose : Purpose.values()) {
            if (purpose.name.equals(name)) {
                return purpose;
            }
        }

        throw new UnsupportedPasetoException("Could not parse Purpose from name: " + name);
    }
}
