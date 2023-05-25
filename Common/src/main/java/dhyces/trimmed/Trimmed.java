package dhyces.trimmed;

import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Trimmed {
    public static final String MODID = "trimmed";
    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");
}
