package dhyces.modhelper.network;

import dhyces.modhelper.services.Services;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

public class ForgeChannelHandler extends SimpleChannelHandler {

    public final SimpleChannel channel;
    public int packetId = 0;

    public ForgeChannelHandler(String version, ResourceLocation id) {
        channel = NetworkRegistry.newSimpleChannel(id, () -> version, version::equals, version::equals);
    }

    @Override
    public <P extends Player> void registerPacket(SimplePacket.Type<?> packetType, SimpleNetworkHandler.Handler<?, P> packetHandler, Designation designation) {
        if (designation.isClientHandled()) {
            Services.CLIENT_HELPER.registerClientPacket(this, packetType, packetHandler);
        } else {
            channel.messageBuilder(packetType.clazz(), packetId++, NetworkDirection.PLAY_TO_SERVER)
                    .encoder(SimplePacket::writeTo)
                    .decoder(friendlyByteBuf -> packetType.factory().apply(friendlyByteBuf).cast())
                    .consumerMainThread((packet, contextSupplier) -> {
                        packetHandler.handlePacket(packet.cast(), (P) contextSupplier.get().getSender(), SimpleNegotiator.simpleWrapper(contextSupplier.get().getPacketDispatcher()::sendPacket));
                    }).add();
        }
    }
}
