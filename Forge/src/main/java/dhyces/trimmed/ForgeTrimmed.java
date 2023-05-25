package dhyces.trimmed;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod("trimmed")
public class ForgeTrimmed {
    public ForgeTrimmed() {
        if (FMLLoader.getDist().isClient()) {
            ForgeTrimmedClient.init(MinecraftForge.EVENT_BUS, FMLJavaModLoadingContext.get().getModEventBus());
        }
    }
}
