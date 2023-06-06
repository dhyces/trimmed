package dhyces.modhelper.network;

import net.minecraft.server.level.ServerPlayer;

public class SimpleServerNetworkHandler extends SimpleNetworkHandler<ServerPlayer> {
    public SimpleServerNetworkHandler() {
        super(Designation.SERVER);
    }
}
