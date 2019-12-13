package dev.paseto.jpaseto.impl;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.PasetoV1PublicBuilder;
import dev.paseto.jpaseto.impl.crypto.V1PublicCryptoProvider;
import dev.paseto.jpaseto.lang.Services;

import java.security.PrivateKey;

@AutoService(PasetoV1PublicBuilder.class)
public class DefaultPasetoV1PublicBuilder extends AbstractPasetoBuilder<PasetoV1PublicBuilder> implements PasetoV1PublicBuilder {

    private static final String HEADER = "v1.public.";

    private PrivateKey privateKey;

    private final V1PublicCryptoProvider cryptoProvider;

    public DefaultPasetoV1PublicBuilder() {
        this(Services.loadFirst(V1PublicCryptoProvider.class));
    }

    private DefaultPasetoV1PublicBuilder(V1PublicCryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    @Override
    public PasetoV1PublicBuilder setPrivateKey(PrivateKey privateKey) {
        this.privateKey = privateKey;
        return this;
    }

    @Override
    public String compact() {

        byte[] payload = payloadAsBytes();
        byte[] footer = footerAsBytes();

        byte[] signature = cryptoProvider.sign(payload, footer, privateKey);

        return HEADER + noPadBase64(payload, signature) + footerToString(footer);
    }
}
