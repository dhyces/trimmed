package dhyces.testmod;

import dhyces.testmod.client.providers.MyProviderTypes;
import dhyces.testmod.data.TestDatagen;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TrimmedTest.MODID)
public class TrimmedTest {

    public static final String MODID = "testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public TrimmedTest() {
        MyProviderTypes.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.init(modBus);

        if (FMLLoader.getDist().isClient()) {
            TrimmedTestClient.init(MinecraftForge.EVENT_BUS, modBus);
        }

        if (DatagenModLoader.isRunningDataGen()) {
            TestDatagen.init(modBus);
        }
    }
}
