package dhyces.testmod;

import com.google.common.collect.BiMap;
import com.mojang.serialization.DataResult;
import dhyces.testmod.client.providers.MyProviderTypes;
import dhyces.testmod.data.TestDatagen;
import dhyces.testmod.registry.CustomRegistration;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedBiMap;
import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TrimmedTest.MODID)
public class TrimmedTest {

    public static final String MODID = "trimmed_testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final LimitedMap<DamageType, String> TEST_DELEGATE = ClientMapManager.getRegistryHandler(Registries.DAMAGE_TYPE).getMap(TestClientMaps.DATAGEN_TEST_DAMAGE_TYPE_MAP);

    public static final LimitedBiMap<EntityType<?>, String> TEST_DELEGATE_2 = ClientMapManager.getRegistryHandler(Registries.ENTITY_TYPE).getBiMap(TestClientMaps.DATAGEN_ENTITY_TRANSFORM);

    public TrimmedTest() {
        MyProviderTypes.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.init(modBus);
        ModTabs.register(modBus);
        CustomRegistration.CUSTOM_DEFERRED_REGISTRY.register(modBus);

        if (FMLLoader.getDist().isClient()) {
            TrimmedTestClient.init(modBus, MinecraftForge.EVENT_BUS);
        }

        if (DatagenModLoader.isRunningDataGen()) {
            TestDatagen.init(modBus);
        }

        LimitedMap<ResourceLocation, MapValue> test = ClientMapManager.getUncheckedHandler().getMap(TestClientMaps.DATAGEN_TEST_MAP_2);
    }
}
