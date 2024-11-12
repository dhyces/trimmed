package dev.dhyces.trimmed.api.client.map;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.resources.ResourceLocation;

public final class ClientMapTypes {
    private ClientMapTypes() {}
    public static void init() {}

    private static final BiMap<ResourceLocation, MapType<?, ?>> REGISTERED_TYPES = HashBiMap.create();
    public static final Codec<MapType<?, ?>> CODEC = CodecUtil.TRIMMED_IDENTIFIER.xmap(REGISTERED_TYPES::get, REGISTERED_TYPES.inverse()::get);

    /**
     * This is not a required step to create a map type, though it does allow it to be represented as a map type in
     * data such as model generators
     */
    public static <K, V> MapType<K, V> registerType(ResourceLocation id, MapType<K, V> mapType) {
        if (REGISTERED_TYPES.putIfAbsent(id, mapType) != null) {
            throw new IllegalArgumentException("Map type with id " + id + " already registered");
        }
        return mapType;
    }

    public static final MapType<ResourceLocation, String> TEXTURE_SUFFIX = registerType(TrimmedReference.id("texture_suffix"), MapType.simpleBuilder(ClientKeyResolvers.TEXTURE, Codec.STRING).build());
    public static final MapType<ResourceLocation, ResourceLocation> TEXTURE_MAPPING = registerType(TrimmedReference.id("texture_mapping"), MapType.simpleBuilder(ClientKeyResolvers.TEXTURE, ResourceLocation.CODEC).build());
}
