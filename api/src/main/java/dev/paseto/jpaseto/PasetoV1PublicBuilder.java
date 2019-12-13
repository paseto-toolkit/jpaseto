package dev.paseto.jpaseto;

import java.security.PrivateKey;

public interface PasetoV1PublicBuilder extends PasetoBuilder<PasetoV1PublicBuilder> {

    PasetoV1PublicBuilder setPrivateKey(PrivateKey privateKey);
}
