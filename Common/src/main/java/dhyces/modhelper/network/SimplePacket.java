package dhyces.modhelper.network;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface SimplePacket<T extends SimplePacket<T>> {
    void writeTo(FriendlyByteBuf friendlyByteBuf);

    Type<?> getType();

    default <T> T cast() {
        return (T)this;
    }

    interface PacketFactory<T extends SimplePacket<T>> extends Function<FriendlyByteBuf, T> {

    }

    record Type<T extends SimplePacket<T>>(Class<T> clazz, ResourceLocation id, PacketFactory<T> factory) {

    }
}
