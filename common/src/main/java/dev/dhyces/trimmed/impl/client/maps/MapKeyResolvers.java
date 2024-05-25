package dev.dhyces.trimmed.impl.client.maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.ApiStatus;

public final class MapKeyResolvers {
    private static final BiMap<ResourceLocation, MapKeyResolver<?>> CUSTOM_RESOLVERS = HashBiMap.create();
    static {
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            CUSTOM_RESOLVERS.put(registry.key().location(), new MapKeyResolver.RegistryWrapper<>(registry));
        }
    }

    public static final MapKeyResolver<Block> BLOCK = getResolver(Registries.BLOCK.location());

    @ApiStatus.Internal
    public static <T> void register(ResourceLocation key, MapKeyResolver<T> resolver) {
        if (CUSTOM_RESOLVERS.putIfAbsent(key, resolver) != null) {
            throw new IllegalArgumentException("Mapping already registered for %s".formatted(key));
        }
    }

    public static <T> MapKeyResolver<T> getResolver(ResourceLocation key) {
        return (MapKeyResolver<T>) CUSTOM_RESOLVERS.get(key);
    }

    public static <T> ResourceLocation getId(MapKeyResolver<T> key) {
        return CUSTOM_RESOLVERS.inverse().get(key);
    }
}
