package dhyces.modhelper.network;

import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Player;

import java.util.function.Supplier;

public abstract class SimpleChannelHandler {

    public <P extends Player> void registerHandler(Supplier<SimpleNetworkHandler<P>> handlerSupplier) {
        SimpleNetworkHandler<P> networkHandler = handlerSupplier.get();
        networkHandler.handlers.forEach((type, handler) -> registerPacket(type, handler, networkHandler.getDesignation()));
    }

    protected abstract <P extends Player> void registerPacket(SimplePacket.Type<?> packetType, SimpleNetworkHandler.Handler<?, P> packetHandler, Designation designation);

    public static FriendlyByteBuf unpooled() {
        return new FriendlyByteBuf(Unpooled.buffer());
    }
}
