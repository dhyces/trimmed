package dhyces.testmod;

import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Block;

public class TestClientMaps {
    // MANUAL
    public static final ClientMapKey MANUAL_TEST_MAP = ClientMapKey.of(TrimmedTest.id("test_map"));
    public static final ClientRegistryMapKey<Item> MANUAL_TEST_ITEM_MAP = ClientRegistryMapKey.of(Registries.ITEM, TrimmedTest.id("checked_item_map"));
    public static final ClientRegistryMapKey<Biome> MANUAL_TEST_BIOME_MAP = ClientRegistryMapKey.of(Registries.BIOME, TrimmedTest.id("checked_biome_map"));
    public static final ClientRegistryMapKey<Block> MANUAL_SCANNER_DESCS = ClientRegistryMapKey.of(Registries.BLOCK, TrimmedTest.id("scanner_descriptors"));

    // DATAGENNED
    public static final ClientMapKey DATAGEN_TEST_MAP_2 = ClientMapKey.of(TrimmedTest.id("test_map_2"));
    public static final ClientRegistryMapKey<Block> DATAGEN_TEST_BLOCK_MAP = ClientRegistryMapKey.of(Registries.BLOCK, TrimmedTest.id("registry_block_map"));
    public static final ClientRegistryMapKey<DamageType> DATAGEN_TEST_DAMAGE_TYPE_MAP = ClientRegistryMapKey.of(Registries.DAMAGE_TYPE, TrimmedTest.id("datapacked_damage_type_map"));
    public static final ClientRegistryMapKey<EntityType<?>> DATAGEN_ENTITY_TRANSFORM = ClientRegistryMapKey.of(Registries.ENTITY_TYPE, TrimmedTest.id("entity_transform"));
}
