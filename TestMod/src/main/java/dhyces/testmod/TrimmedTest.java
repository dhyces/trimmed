package dhyces.testmod;

import com.google.common.collect.BiMap;
import com.mojang.serialization.DataResult;
import dhyces.testmod.client.providers.MyProviderTypes;
import dhyces.testmod.data.TestDatagen;
import dhyces.testmod.registry.CustomObj;
import dhyces.testmod.registry.CustomRegistration;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dhyces.trimmed.impl.client.maps.manager.delegates.BiMapMapDelegate;
import dhyces.trimmed.impl.client.maps.manager.delegates.HashMapDelegate;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.data.loading.DatagenModLoader;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import org.jetbrains.annotations.UnmodifiableView;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Supplier;

@Mod(TrimmedTest.MODID)
public class TrimmedTest {

    public static final String MODID = "testmod";
    public static final Logger LOGGER = LoggerFactory.getLogger(MODID);

    public static ResourceLocation id(String id) {
        return new ResourceLocation(MODID, id);
    }

    public static final BiMap<Integer, Holder<DamageType>> TEST_DELEGATE = ClientMapManager.getDatapackedHandler(Registries.DAMAGE_TYPE).biMapDelegate(TestClientMaps.DATAGEN_TEST_DAMAGE_TYPE_MAP, (r, s) -> DataResult.success(Integer.decode(s))).inverse();

    public static final BiMap<EntityType<?>, EntityType<?>> TEST_DELEGATE_2 = ClientMapManager.getRegistryHandler(Registries.ENTITY_TYPE).biMapDelegate(TestClientMaps.DATAGEN_ENTITY_TRANSFORM, s -> DataResult.success(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(s))), s -> DataResult.success(BuiltInRegistries.ENTITY_TYPE.get(new ResourceLocation(s))));

    public TrimmedTest() {
        MyProviderTypes.init();
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        ModItems.init(modBus);
        CustomRegistration.CUSTOM_DEFERRED_REGISTRY.register(modBus);

        if (FMLLoader.getDist().isClient()) {
            TrimmedTestClient.init(modBus, MinecraftForge.EVENT_BUS);
        }

        if (DatagenModLoader.isRunningDataGen()) {
            TestDatagen.init(modBus);
        }

        HashMapDelegate<OptionalId, Object> test = ClientMapManager.getUncheckedHandler().hashMapDelegate(TestClientMaps.DATAGEN_TEST_MAP_2, DataResult::success);
    }
}
