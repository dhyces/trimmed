package dhyces.trimmed.impl.network;

import dhyces.modhelper.network.SimplePacket;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.network.packets.SyncMapsPacket;
import net.minecraft.world.item.ItemStack;

public final class Packets {
    public static final SimplePacket.Type<SyncMapsPacket> SYNC_MAPS_TYPE = new SimplePacket.Type<>(SyncMapsPacket.class, Trimmed.id("sync_maps"), friendlyByteBuf -> {
        ItemStack item = friendlyByteBuf.readItem();
        return new SyncMapsPacket(item);
    });
}
