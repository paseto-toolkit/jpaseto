package dev.paseto.jpaseto.crypto.bouncycastle;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.impl.crypto.JcaV1PublicCryptoProvider;
import dev.paseto.jpaseto.impl.crypto.V1PublicCryptoProvider;

@AutoService(V1PublicCryptoProvider.class)
public class BouncyCastleV1PublicCryptoProvider extends JcaV1PublicCryptoProvider implements V1PublicCryptoProvider {

    {
        BouncyCastleInitializer.enableBouncyCastle();
    }
}
