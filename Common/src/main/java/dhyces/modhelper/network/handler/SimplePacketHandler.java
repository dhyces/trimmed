package dhyces.modhelper.network.handler;

import dhyces.modhelper.network.packet.CommonPacket;

import java.util.HashMap;
import java.util.Map;

public class SimplePacketHandler implements PacketHandler {
    private final Map<CommonPacket.Type<?>, PacketConsumer> associations;

    public SimplePacketHandler() {
        associations = new HashMap<>();
    }

    @Override
    public <T extends CommonPacket<T>> PacketConsumer getPacketConsumer(CommonPacket.Type<T> packetType) {
        return associations.get(packetType);
    }

    @Override
    public <T extends CommonPacket<T>> void registerPacketConsumer(CommonPacket.Type<T> packetType, PacketConsumer.Typed<T> consumer) {
        associations.put(packetType, consumer);
    }
}
