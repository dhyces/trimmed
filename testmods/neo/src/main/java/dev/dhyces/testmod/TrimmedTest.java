package dev.dhyces.testmod;

import dev.dhyces.testmod.data.TestDatagen;
import dev.dhyces.testmod.registry.ModArmorMaterials;
import dev.dhyces.testmod.registry.ModItems;
import dev.dhyces.testmod.registry.ModTabs;
import dev.dhyces.testmod.registry.custom.CustomRegistration;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.loading.DatagenModLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(TrimmedTest.MODID)
public class TrimmedTest {
    public static final String MODID = "testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

//    public static final MapHolder<DamageType, String> TEST_DELEGATE = ClientMapManager.getRegistryHandler(Registries.DAMAGE_TYPE).getMap(TestClientMapKeys.DATAGEN_TEST_DAMAGE_TYPE_MAP);

//    public static final MapHolder.Typed<EntityType<?>, String, BiMap<EntityType<?>, String>> TEST_DELEGATE_2 = ClientMapManager.getRegistryHandler(Registries.ENTITY_TYPE).getBiMap(TestClientMapKeys.DATAGEN_ENTITY_TRANSFORM);

    public TrimmedTest(IEventBus modBus) {
        ModArmorMaterials.REGISTER.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModTabs.REGISTER.register(modBus);
        CustomRegistration.CUSTOM_DEFERRED_REGISTRY.register(modBus);

        if (DatagenModLoader.isRunningDataGen()) {
            TestDatagen.init(modBus);
        }
    }
}
