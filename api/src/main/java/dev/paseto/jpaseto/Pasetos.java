package dev.paseto.jpaseto;

import dev.paseto.jpaseto.lang.Services;

public final class Pasetos {

    public static PasetosV2Local v2Local() {
        return new PasetosV2Local();
    }

    public static PasetosV2Public v2Public() {
        return new PasetosV2Public();
    }

    public static PasetosV1Local v1Local() {
        return new PasetosV1Local();
    }

    public static PasetosV1Public v1Public() {
        return new PasetosV1Public();
    }

    public static PasetoParserBuilder parserBuilder() {
        return Services.loadFirst(PasetoParserBuilder.class);
    }

    public static class PasetosV2Local {
        public PasetoV2LocalBuilder builder() {
            return Services.loadFirst(PasetoV2LocalBuilder.class);
        }
    }

    public static class PasetosV2Public {
        public PasetoV2PublicBuilder builder() {
            return Services.loadFirst(PasetoV2PublicBuilder.class);
        }
    }

    public static class PasetosV1Local {
        public PasetoV1LocalBuilder builder() {
            return Services.loadFirst(PasetoV1LocalBuilder.class);
        }
    }

    public static class PasetosV1Public {
        public PasetoV1PublicBuilder builder() {
            return Services.loadFirst(PasetoV1PublicBuilder.class);
        }
    }
}
