package dev.paseto.jpaseto;

import javax.crypto.SecretKey;

public interface PasetoV2LocalBuilder extends PasetoBuilder<PasetoV2LocalBuilder> {

    PasetoV2LocalBuilder setSharedSecret(SecretKey sharedSecret);

}
