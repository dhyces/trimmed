package dev.dhyces.testmod;

import com.google.common.collect.BiMap;
import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.testmod.client.TestClientMapTypes;
import dev.dhyces.testmod.data.TestDatagen;
import dev.dhyces.testmod.registry.ModArmorMaterials;
import dev.dhyces.testmod.registry.ModItems;
import dev.dhyces.testmod.registry.ModTabs;
import dev.dhyces.testmod.registry.custom.CustomRegistration;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.maps.MapHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
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

    public static final MapHolder.Typed<EntityType<?>, EntityType<?>, BiMap<EntityType<?>, EntityType<?>>> TEST_DELEGATE_2 = TrimmedClientMapApi.getInstance().getAdvancedMap(TestClientMapKeys.DATAGEN_ENTITY_TRANSFORM, TestClientMapTypes.ENTITY_CONVERSION);

    public TrimmedTest(IEventBus modBus) {
        ModArmorMaterials.REGISTER.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModTabs.REGISTER.register(modBus);
        CustomRegistration.CUSTOM_DEFERRED_REGISTRY.register(modBus);
    }
}
