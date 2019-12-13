package dev.paseto.jpaseto.crypto.bouncycastle;

import org.bouncycastle.jce.provider.BouncyCastleProvider;

import java.security.Provider;
import java.security.Security;
import java.util.concurrent.atomic.AtomicBoolean;

final class BouncyCastleInitializer {

    private static final AtomicBoolean bcLoaded = new AtomicBoolean(false);

    private BouncyCastleInitializer() {}

    static void enableBouncyCastle() {
        for(Provider provider : Security.getProviders()) {
            if (BouncyCastleProvider.PROVIDER_NAME.equals(provider.getName())) {
                bcLoaded.set(true);
                return;
            }
        }

        //bc provider not enabled - add it:
        Security.addProvider(new BouncyCastleProvider());
        bcLoaded.set(true);
    }
}
