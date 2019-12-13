package dev.paseto.jpaseto;

import javax.crypto.SecretKey;

public interface PasetoV1LocalBuilder extends PasetoBuilder<PasetoV1LocalBuilder> {
    PasetoV1LocalBuilder setSharedSecret(SecretKey sharedSecret);
}
