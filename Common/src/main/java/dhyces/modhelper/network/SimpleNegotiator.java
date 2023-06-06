package dhyces.modhelper.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public interface SimpleNegotiator {
    void send(SimplePacket<?> packet);

    static SimpleNegotiator simpleWrapper(BiConsumer<ResourceLocation, FriendlyByteBuf> consumer) {
        return packet -> {
            FriendlyByteBuf byteBuf = SimpleChannelHandler.unpooled();
            packet.writeTo(byteBuf);
            consumer.accept(packet.getType().id(), byteBuf);
        };
    }
}
