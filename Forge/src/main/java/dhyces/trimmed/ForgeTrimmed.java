package dhyces.trimmed;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;

@Mod(Trimmed.MODID)
public class ForgeTrimmed {
    public ForgeTrimmed() {
        Trimmed.init();
        if (FMLLoader.getDist().isClient()) {
            ForgeTrimmedClient.init(MinecraftForge.EVENT_BUS, FMLJavaModLoadingContext.get().getModEventBus());
        }
    }
}
