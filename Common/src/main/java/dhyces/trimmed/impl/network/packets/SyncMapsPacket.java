package dhyces.trimmed.impl.network.packets;

import dhyces.modhelper.network.SimplePacket;
import dhyces.trimmed.impl.network.Packets;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public record SyncMapsPacket(ItemStack item) implements SimplePacket<SyncMapsPacket> {

    @Override
    public void writeTo(FriendlyByteBuf friendlyByteBuf) {
        friendlyByteBuf.writeItem(item);
    }

    @Override
    public Type<?> getType() {
        return Packets.SYNC_MAPS_TYPE;
    }
}
