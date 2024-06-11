package dev.dhyces.trimmed.impl.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.maps.types.AdvancedMapType;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
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
        return MapKey.codec(mapType).flatComapMap(ClientMapManager::getHolder, kvMapHolder ->
            kvMapHolder.getKey().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No key is present for map holder"))
        );
    }

    @Override
    public <K, V, M extends Map<K, V>> MapHolder.Typed<K, V, M> getAdvancedMap(MapKey<K, V> key, AdvancedMapType<K, V, M> mapType) {
        return (MapHolder.Typed<K, V, M>) ClientMapManager.getHolder(key);
    }

    @Override
    public <K, V, M extends Map<K, V>> Codec<MapHolder.Typed<K, V, M>> advancedCodec(AdvancedMapType<K, V, M> mapType) {
        return MapKey.codec(mapType).flatComapMap(kvMapKey -> (MapHolder.Typed<K, V, M>) ClientMapManager.getHolder(kvMapKey), kvmTyped ->
                kvmTyped.getKey().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No key is present for map holder"))
        );
    }

    @Override
    public @Nullable <T> KeyResolver.RegistryResolver<T> getRegistryKeyResolver(ResourceKey<? extends Registry<T>> key) {
        return KeyResolvers.getRegistryResolver(key);
    }
}
