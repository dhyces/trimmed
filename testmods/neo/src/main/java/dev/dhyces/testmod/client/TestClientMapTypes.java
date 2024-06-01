package dev.dhyces.testmod.client;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.maps.types.AdvancedMapType;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

public class TestClientMapTypes {
    public static final MapType<ResourceLocation, String> CLIENT_RESOLVER_TEST = MapType.simple(TestClientKeyResolvers.TEST, Codec.STRING);
    public static final MapType<Block, String> SCANNER_DESCRIPTORS = MapType.simple(KeyResolvers.BLOCK, Codec.STRING);
    public static final MapType<Item, String> ITEM_TEST = MapType.simple(KeyResolvers.ITEM, Codec.STRING);
    public static final MapType<Block, String> BLOCK_TEST = MapType.simple(KeyResolvers.BLOCK, Codec.STRING);
    public static final AdvancedMapType<EntityType<?>, EntityType<?>, BiMap<EntityType<?>, EntityType<?>>> ENTITY_CONVERSION = MapType.advancedCollection(KeyResolvers.ENTITY_TYPE, BuiltInRegistries.ENTITY_TYPE.byNameCodec(), HashBiMap::create);
//    public static final MapType<Biome, Integer> BIOME_TEST = MapType.simple(KeyResolvers.BIOME, Codec.INT);
}
