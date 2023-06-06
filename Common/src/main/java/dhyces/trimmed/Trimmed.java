package dhyces.trimmed;

import dhyces.modhelper.services.Services;
import dhyces.trimmed.impl.network.Networking;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimmed {
    public static final String MODID = "trimmed";
    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");

    public static void init() {
        Networking.init();
    }

    public static void logInDev(String str) {
        if (!Services.PLATFORM_HELPER.isProduction()) {
            LOGGER.info(str);
        }
    }
}
