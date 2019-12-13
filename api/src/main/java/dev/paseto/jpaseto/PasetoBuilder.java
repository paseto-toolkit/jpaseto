package dev.paseto.jpaseto;

import dev.paseto.jpaseto.io.Serializer;

import java.util.Map;

public interface PasetoBuilder<T extends PasetoBuilder> extends ClaimsMutator<T> {

    T setSerializer(Serializer<Map<String, ?>> serializer);

    String compact();
}
