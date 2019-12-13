package dev.paseto.jpaseto.impl;

import dev.paseto.jpaseto.impl.crypto.JcaV1PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V2LocalCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V2PublicCryptoProvider;
import dev.paseto.jpaseto.lang.Services;

final class CryptoProviders {

    private CryptoProviders() {}

    static V1LocalCryptoProvider v1LocalCryptoProvider() {
        return  Services.loadFirst(V1LocalCryptoProvider.class);
    }

    static V1PublicCryptoProvider v1PublicCryptoProvider() {
        return  Services.loadFirst(V1PublicCryptoProvider.class, new JcaV1PublicCryptoProvider());
    }

    static V2LocalCryptoProvider v2LocalCryptoProvider() {
        return  Services.loadFirst(V2LocalCryptoProvider.class);
    }

    static V2PublicCryptoProvider v2PublicCryptoProvider() {
        return  Services.loadFirst(V2PublicCryptoProvider.class);
    }
}
