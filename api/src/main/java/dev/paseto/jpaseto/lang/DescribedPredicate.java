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
package dev.paseto.jpaseto.lang;

import java.util.Objects;
import java.util.function.Predicate;

public final class DescribedPredicate<T> implements Predicate<T> {

    private final String description;
    private final Predicate<T> predicate;

    public DescribedPredicate(String description, Predicate<T> predicate) {
        this.description = description;
        this.predicate = predicate;
    }

    @Override
    public boolean test(T t) {
        return predicate.test(t);
    }

    public String getDescription() {
        return description;
    }

    public static <T> Predicate<T> equalTo(T value) {
        return new DescribedPredicate<>("equal to: '" + value + "'", o -> Objects.equals(value, o));
    }
}
