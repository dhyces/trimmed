package dhyces.trimmed;

import net.minecraft.resources.ResourceLocation;
import org.quiltmc.loader.api.ModContainer;
import org.quiltmc.loader.api.QuiltLoader;
import org.quiltmc.qsl.base.api.entrypoint.ModInitializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class QuiltTrimmed implements ModInitializer {
    public static final String MODID = "trimmed";
    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");

    public static void logInDev(String str) {
        if (QuiltLoader.isDevelopmentEnvironment()) {
            LOGGER.info(str);
        }
    }

    @Override
    public void onInitialize(ModContainer mod) {

    }
}
