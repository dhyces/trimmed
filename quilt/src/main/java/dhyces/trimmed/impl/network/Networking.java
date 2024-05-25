package dhyces.trimmed.impl.network;

import commonnetwork.api.Network;
import commonnetwork.networking.data.PacketContext;
import dhyces.trimmed.modhelper.network.packet.CommonPacket;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.TrimmedClient;
import dhyces.trimmed.impl.network.packets.SyncMapsPacket;

import java.util.function.Consumer;

public class Networking {

    public static final CommonPacket.Type<SyncMapsPacket> SYNC_MAPS = new CommonPacket.Type<>(SyncMapsPacket.class, Trimmed.id("sync_maps"), SyncMapsPacket::new);

    public static void init() {
        registerPacket(SYNC_MAPS, packet -> TrimmedClient.CLIENT_HANDLER.getPacketConsumer(SYNC_MAPS).handle(packet));
    }

    private static <T extends CommonPacket<T>> void registerPacket(CommonPacket.Type<T> type, Consumer<PacketContext<T>> packetHandler) {
        Network.registerPacket(type.id(), type.clazzType(), CommonPacket::writeTo, type.factory(), packetHandler);
    }
}
