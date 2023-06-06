package dhyces.trimmed.impl.network;

import commonnetwork.api.Network;
import dhyces.modhelper.network.handler.PacketHandler;
import dhyces.modhelper.network.packet.CommonPacket;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.TrimmedClient;
import dhyces.trimmed.impl.network.packets.SyncMapsPacket;

public class Networking {

    public static final CommonPacket.Type<SyncMapsPacket> SYNC_MAPS = new CommonPacket.Type<>(SyncMapsPacket.class, Trimmed.id("sync_maps"), SyncMapsPacket::new);

    public static void init() {
        registerPacket(SYNC_MAPS, TrimmedClient.CLIENT_HANDLER);
    }

    private static <T extends CommonPacket<T>> void registerPacket(CommonPacket.Type<T> type, PacketHandler packetHandler) {
        Network.registerPacket(type.id(), type.clazzType(), CommonPacket::writeTo, type.factory(), packetHandler.getPacketConsumer(type)::handle);
    }
}
