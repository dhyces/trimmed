package dev.dhyces.trimmed.impl.client;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import dev.dhyces.trimmed.api.maps.types.AdvancedMapType;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class TrimmedClientMapApiImpl implements TrimmedClientMapApi {
    private TrimmedClientMapApiImpl() {}
    public static final TrimmedClientMapApi INSTANCE = new TrimmedClientMapApiImpl();

    @Override
    public <K, V> MapHolder<K, V> getSimpleMap(MapKey<K, V> key) {
        return ClientMapManager.getHolder(key);
    }

    @Override
    public <K, V> Codec<MapHolder<K, V>> simpleCodec(MapType<K, V> mapType) {
        return MapKey.codec(mapType).xmap(ClientMapManager::getHolder, MapHolder::unwrapKeyOrThrow);
    }

    @Override
    public <K, V, M extends Map<K, V>> MapHolder.Typed<K, V, M> getAdvancedMap(MapKey<K, V> key, AdvancedMapType<K, V, M> mapType) {
        return (MapHolder.Typed<K, V, M>) ClientMapManager.getHolder(key);
    }

    @Override
    public <K, V, M extends Map<K, V>> Codec<MapHolder.Typed<K, V, M>> advancedCodec(AdvancedMapType<K, V, M> mapType) {
        return MapKey.codec(mapType).xmap(kvMapKey -> (MapHolder.Typed<K, V, M>) ClientMapManager.getHolder(kvMapKey), MapHolder::unwrapKeyOrThrow);
    }

    @Override
    public @Nullable <T> MapKeyResolver<T> getRegistryMapKeyResolver(ResourceKey<? extends Registry<T>> id) {
        return MapKeyResolvers.getResolver(id.location());
    }
}
