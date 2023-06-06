package dhyces.modhelper.network;

import dhyces.modhelper.services.Services;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.world.entity.player.Player;

public class FabricChannelHandler extends SimpleChannelHandler {
    @Override
    public <P extends Player> void registerPacket(SimplePacket.Type<?> packetType, SimpleNetworkHandler.Handler<?, P> packetHandler, Designation designation) {
        if (designation.isClientHandled()) {
            Services.CLIENT_HELPER.registerClientPacket(this, packetType, packetHandler);
        } else {
            ServerPlayNetworking.registerGlobalReceiver(packetType.id(), (server, player, handler, buf, responseSender) -> {
                SimplePacket<?> packet = packetType.factory().apply(buf);
                server.execute(() -> packetHandler.handlePacket(packet.cast(), (P) player, SimpleNegotiator.simpleWrapper(responseSender::sendPacket)));
            });
        }
    }
}
