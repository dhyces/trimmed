package dev.dhyces.trimmed.impl;

import dev.dhyces.trimmed.api.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.maps.types.AdvancedMapType;
import dev.dhyces.trimmed.impl.client.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;

import java.util.Map;

public final class TrimmedClientMapApiImpl implements TrimmedClientMapApi {
    public static final TrimmedClientMapApi INSTANCE = new TrimmedClientMapApiImpl();

    @Override
    public <K, V> MapHolder<K, V> getSimpleMap(MapKey<K, V> key) {
        return ClientMapManager.getHolder(key);
    }

    @Override
    public <K, V, M extends Map<K, V>> MapHolder.Typed<K, V, M> getAdvancedMap(MapKey<K, V> key, AdvancedMapType<K, V, M> mapType) {
        return (MapHolder.Typed<K, V, M>) ClientMapManager.getHolder(key);
    }
}
