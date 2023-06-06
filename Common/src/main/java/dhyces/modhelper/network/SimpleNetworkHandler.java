package dhyces.modhelper.network;

import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.HashMap;
import java.util.Map;

public abstract class SimpleNetworkHandler<P extends Player> {
    final Map<SimplePacket.Type<?>, Handler<?, P>> handlers = new HashMap<>();
    private final Designation designation;

    public SimpleNetworkHandler(Designation designation) {
        this.designation = designation;
    }

    public Designation getDesignation() {
        return designation;
    }

    public <T extends SimplePacket<T>, HANDLER extends Handler<T, P>> void registerHandler(SimplePacket.Type<T> packetType, HANDLER handler) {
        if (handlers.containsKey(packetType)) {
            throw new IllegalStateException("Cannot register more than one handler for a packet!");
        }
        handlers.put(packetType, handler);
    }

    public interface Handler<T extends SimplePacket<T>, P extends Player> {
        void handlePacket(T packet, P player, SimpleNegotiator negotiator);
    }
}
