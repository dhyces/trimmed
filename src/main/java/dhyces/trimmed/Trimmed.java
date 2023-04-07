package dhyces.trimmed;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("trimmed")
public class Trimmed {
    public static final String MODID = "trimmed";
    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final Logger LOGGER = LoggerFactory.getLogger("Trimmed");

    public Trimmed() {
        if (FMLLoader.getDist().isClient()) {
            TrimmedClient.init(FMLJavaModLoadingContext.get().getModEventBus());
        }
    }
}
