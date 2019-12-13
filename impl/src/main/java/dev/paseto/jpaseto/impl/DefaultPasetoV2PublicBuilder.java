package dev.paseto.jpaseto.impl;

import com.google.auto.service.AutoService;
import dev.paseto.jpaseto.PasetoV2PublicBuilder;
import dev.paseto.jpaseto.impl.crypto.V2PublicCryptoProvider;
import dev.paseto.jpaseto.lang.Services;
import java.security.PrivateKey;

@AutoService(PasetoV2PublicBuilder.class)
public class DefaultPasetoV2PublicBuilder extends AbstractPasetoBuilder<PasetoV2PublicBuilder> implements PasetoV2PublicBuilder {

    private static final String HEADER = "v2.public.";

    private PrivateKey privateKey;

    private final V2PublicCryptoProvider cryptoProvider;

    public DefaultPasetoV2PublicBuilder() {
        this(Services.loadFirst(V2PublicCryptoProvider.class));
    }

    private DefaultPasetoV2PublicBuilder(V2PublicCryptoProvider cryptoProvider) {
        this.cryptoProvider = cryptoProvider;
    }

    @Override
    public PasetoV2PublicBuilder setPrivateKey(PrivateKey privateKey) {
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
