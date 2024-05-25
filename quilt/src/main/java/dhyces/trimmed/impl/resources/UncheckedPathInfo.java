package dhyces.trimmed.impl.resources;

public final class UncheckedPathInfo implements PathInfo {
    public static final UncheckedPathInfo INSTANCE = new UncheckedPathInfo();

    private UncheckedPathInfo() {}

    @Override
    public String getPath() {
        return "unchecked";
    }
}