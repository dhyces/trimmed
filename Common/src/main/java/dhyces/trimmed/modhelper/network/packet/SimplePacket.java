package dhyces.trimmed.modhelper.network.packet;

public abstract class SimplePacket<T extends SimplePacket<T>> implements CommonPacket<T> {
    private final Type<T> packetType;

    public SimplePacket(Type<T> packetType) {
        this.packetType = packetType;
    }

    @Override
    public Type<?> getType() {
        return packetType;
    }
}
