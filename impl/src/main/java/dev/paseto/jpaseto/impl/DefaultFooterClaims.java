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