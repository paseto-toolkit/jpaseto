package dev.paseto.jpaseto;

import java.security.PrivateKey;

public interface PasetoV2PublicBuilder extends PasetoBuilder<PasetoV2PublicBuilder> {

    PasetoV2PublicBuilder setPrivateKey(PrivateKey privateKey);
}
