package dhyces.modhelper.network;

public enum Designation {
    CLIENT, SERVER;

    public boolean isClientHandled() {
        return this == CLIENT;
    }
}
