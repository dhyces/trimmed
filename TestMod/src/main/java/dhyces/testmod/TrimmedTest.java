package dhyces.testmod;

import dhyces.testmod.client.providers.MyProviderTypes;
import net.fabricmc.api.ModInitializer;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TrimmedTest implements ModInitializer {

    public static final String MODID = "testmod";

    public static Identifier id(String id) {
        return new Identifier(MODID, id);
    }

    @Override
    public void onInitialize() {
        MyProviderTypes.init();
    }
}
