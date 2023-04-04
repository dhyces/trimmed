package dhyces.testmod;

import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrimmedTest implements ModInitializer {

    public static final String MODID = "trimmed";
    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }

    @Override
    public void onInitialize() {

    }
}
