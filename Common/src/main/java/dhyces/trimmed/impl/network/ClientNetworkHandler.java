package dhyces.trimmed.impl.network;

import dhyces.modhelper.network.SimpleClientNetworkHandler;
import dhyces.modhelper.network.SimpleNegotiator;
import dhyces.modhelper.network.SimplePacket;
import dhyces.trimmed.impl.network.packets.SyncMapsPacket;
import net.minecraft.client.player.LocalPlayer;

public class ClientNetworkHandler extends SimpleClientNetworkHandler {
    public ClientNetworkHandler() {
        registerHandler(Packets.SYNC_MAPS_TYPE, this::handleSyncMaps);
    }

    private void handleSyncMaps(SyncMapsPacket packet, LocalPlayer player, SimpleNegotiator negotiator) {
        player.displayClientMessage(packet.item().getDisplayName(), false);
    }
}
