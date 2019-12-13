package dev.paseto.jpaseto.impl;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.PasetoV1LocalBuilder;
import dev.paseto.jpaseto.impl.crypto.V1LocalCryptoProvider;
import dev.paseto.jpaseto.lang.Services;

import javax.crypto.SecretKey;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;


@AutoService(PasetoV1LocalBuilder.class)
public class DefaultPasetoV1LocalBuilder extends AbstractPasetoBuilder<PasetoV1LocalBuilder> implements PasetoV1LocalBuilder {

    private String HEADER = "v1.local.";

    private SecretKey sharedSecret = null;

    private final V1LocalCryptoProvider cryptoProvider;

    public DefaultPasetoV1LocalBuilder() {
        this(Services.loadFirst(V1LocalCryptoProvider.class));
    }

    DefaultPasetoV1LocalBuilder(V1LocalCryptoProvider cryptoProvider) {
     this.cryptoProvider = cryptoProvider;
    }

    @Override
    public PasetoV1LocalBuilder setSharedSecret(SecretKey sharedSecret) {
        this.sharedSecret = sharedSecret;
        return this;
    }

    @Override
    public String compact() {

        byte[] payload = payloadAsBytes();
        byte[] footer = footerAsBytes();

        // 2
        byte[] randomBytes = new byte[24];
        try {
            SecureRandom.getInstanceStrong().nextBytes(randomBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new SecurityException("JVM does not provide a strong secure random number generator", e);
        }

//        // FIXME
//        randomBytes = new byte[] {
//                0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,
//                0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,
//                0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0,
//                0,0,0,0,0,0,0,0, 0,0,0,0,0,0,0,0
//        };

        // 3
        byte[] nonce = cryptoProvider.nonce(payload, randomBytes);

        // 4, 5, 6, 7
        byte[] cipherText = cryptoProvider.encrypt(payload, footer, nonce, sharedSecret);

        // 8
        String base64d = noPadBase64(cipherText);
        return HEADER + base64d + footerToString(footer);
    }
}
