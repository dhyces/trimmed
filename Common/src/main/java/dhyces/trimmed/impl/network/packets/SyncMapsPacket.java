package dhyces.trimmed.impl.network.packets;

import dhyces.modhelper.network.packet.SimplePacket;
import dhyces.trimmed.impl.network.Networking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.item.ItemStack;

public class SyncMapsPacket extends SimplePacket<SyncMapsPacket> {

    public final ItemStack stack;

    public SyncMapsPacket(FriendlyByteBuf buf) {
        super(Networking.SYNC_MAPS);
        this.stack = buf.readItem();
    }

    @Override
    public void writeTo(FriendlyByteBuf buf) {
        buf.writeItem(stack);
    }
}
