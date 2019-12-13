package dev.paseto.jpaseto;

public enum Version {
    V1("v1"),
    V2("v2");

    private final String name;

    Version(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }

    public static Version from(String name) {
        for (Version version : Version.values()) {
            if (version.name.equals(name)) {
                return version;
            }
        }

        throw new UnsupportedPasetoException("Could not parse Version from name: " + name);
    }
}
