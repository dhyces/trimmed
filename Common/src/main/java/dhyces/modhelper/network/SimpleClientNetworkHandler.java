package dhyces.modhelper.network;

import net.minecraft.client.player.LocalPlayer;

public class SimpleClientNetworkHandler extends SimpleNetworkHandler<LocalPlayer> {
    public SimpleClientNetworkHandler() {
        super(Designation.CLIENT);
    }

    public interface ClientHandler<T extends SimplePacket<T>> extends Handler<T, LocalPlayer> {
    }
}
