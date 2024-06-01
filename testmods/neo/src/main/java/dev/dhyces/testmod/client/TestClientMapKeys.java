package dev.dhyces.testmod.client;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TestClientMapKeys {
    // MANUAL
    public static final MapKey<ResourceLocation, String> MANUAL_TEST_MAP = MapKey.baseKeyOf(TestClientMapTypes.CLIENT_RESOLVER_TEST, TrimmedTest.id("test_map"));
    public static final MapKey<Block, String> MANUAL_SCANNER_DESCS = MapKey.baseKeyOf(TestClientMapTypes.SCANNER_DESCRIPTORS, TrimmedTest.id("scanner_descriptors"));
    public static final MapKey<Item, String> MANUAL_TEST_ITEM_MAP = MapKey.baseKeyOf(TestClientMapTypes.ITEM_TEST, TrimmedTest.id("checked_item_map"));
//    public static final MapKey<Biome, Integer> MANUAL_TEST_BIOME_MAP = MapKey.of(TestClientMapTypes.BIOME_TEST, TrimmedTest.id("checked_biome_map"));

    // DATAGENNED
    public static final MapKey<ResourceLocation, ResourceLocation> ADAMANTIUM_ARMOR = ClientMapKeys.TRIM_OVERLAYS.makeSubKey(TrimmedTest.id("adamantium_armor"));
    public static final MapKey<ResourceLocation, ResourceLocation> ADAMANTIUM_MATERIAL_OVERRIDES = ClientMapKeys.TRIM_MATERIAL_OVERRIDES.makeSubKey(TrimmedTest.id("adamantium"));
    public static final MapKey<ResourceLocation, String> DATAGEN_TEST_MAP_2 = MapKey.baseKeyOf(TestClientMapTypes.CLIENT_RESOLVER_TEST, TrimmedTest.id("test_map_2"));
    public static final MapKey<Block, String> DATAGEN_TEST_BLOCK_MAP = MapKey.baseKeyOf(TestClientMapTypes.BLOCK_TEST, TrimmedTest.id("registry_block_map"));
    public static final MapKey<EntityType<?>, EntityType<?>> DATAGEN_ENTITY_TRANSFORM = MapKey.baseKeyOf(TestClientMapTypes.ENTITY_CONVERSION, TrimmedTest.id("entity_transform"));
    //    public static final ClientRegistryMapKey<DamageType> DATAGEN_TEST_DAMAGE_TYPE_MAP = ClientRegistryMapKey.of(Registries.DAMAGE_TYPE, TrimmedTest.id("datapacked_damage_type_map"));

}
