package dev.paseto.jpaseto;

import java.util.Map;

public interface FooterClaims extends Map<String, Object> {

    /** Paseto {@code Key ID} claims parameter name: <code>"kid"</code>, registered footer claim. */
    String KEY_ID = "kid";

    <T> T get(String claimName, Class<T> requiredType);

    default String getKeyId() {
        return get(KEY_ID, String.class);
    }

    String value();
}
