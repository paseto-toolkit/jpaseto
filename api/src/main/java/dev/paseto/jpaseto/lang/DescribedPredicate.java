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
        return new DescribedPredicate("equal to: '" + value + "'", o -> Objects.equals(value, o));
    }
}
