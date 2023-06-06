package dhyces.modhelper.network.handler;

import dhyces.modhelper.network.packet.CommonPacket;

public interface PacketHandler {
    <T extends CommonPacket<T>> PacketConsumer getPacketConsumer(CommonPacket.Type<T> packetType);

    <T extends CommonPacket<T>> void registerPacketConsumer(CommonPacket.Type<T> packetType, PacketConsumer.Typed<T> consumer);
}
