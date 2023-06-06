package dhyces.modhelper.network.packet;

import net.minecraft.network.FriendlyByteBuf;

public class MarkerPacket extends SimplePacket<MarkerPacket> {
    public MarkerPacket(Type<MarkerPacket> packetType) {
        super(packetType);
    }

    @Override
    public void writeTo(FriendlyByteBuf buf) {
        // NO-OP
    }
}
