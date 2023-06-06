package dhyces.modhelper.services.helpers;

import dhyces.modhelper.network.ForgeChannelHandler;
import dhyces.modhelper.network.SimpleChannelHandler;
import net.minecraft.resources.ResourceLocation;

public class ForgeNetworkHelper implements NetworkHelper {
    @Override
    public SimpleChannelHandler createChannelHandler(String version, ResourceLocation id) {
        return new ForgeChannelHandler(version, id);
    }
}
