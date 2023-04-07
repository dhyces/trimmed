package dhyces.testmod;

import dhyces.testmod.client.providers.MyProviderTypes;
import dhyces.testmod.data.TrimmedDatagen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(TrimmedTest.MODID)
public class TrimmedTest {

    public static final String MODID = "testmod";

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public TrimmedTest() {
        MyProviderTypes.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.init(modBus);

        if (DatagenModLoader.isRunningDataGen()) {
            TrimmedDatagen.init(modBus);
        }
    }
}
