package dev.paseto.jpaseto;

public enum Purpose {
    LOCAL("local"),
    PUBLIC("public");

    private final String name;

    Purpose(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Purpose from(String name) {
        for (Purpose purpose : Purpose.values()) {
            if (purpose.name.equals(name)) {
                return purpose;
            }
        }

        throw new UnsupportedPasetoException("Could not parse Purpose from name: " + name);
    }
}
