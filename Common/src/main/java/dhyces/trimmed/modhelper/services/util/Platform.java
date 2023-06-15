package dhyces.trimmed.modhelper.services.util;

public enum Platform {
    FABRIC, FORGE, QUILT, UNKNOWN;

    public boolean isFabric() {
        return this == FABRIC;
    }

    public boolean isForge() {
        return this == FORGE;
    }

    public boolean isQuilt() {
        return this == QUILT;
    }

    public boolean isUnknown() {
        return this == UNKNOWN;
    }
}
