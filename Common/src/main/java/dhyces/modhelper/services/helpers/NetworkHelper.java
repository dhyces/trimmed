package dhyces.modhelper.services.helpers;

import dhyces.modhelper.network.SimpleChannelHandler;
import net.minecraft.resources.ResourceLocation;

public interface NetworkHelper {
    SimpleChannelHandler createChannelHandler(String version, ResourceLocation id);
}
