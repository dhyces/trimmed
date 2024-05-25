package dev.dhyces.trimmed.api;

import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.maps.types.AdvancedMapType;
import dev.dhyces.trimmed.impl.TrimmedClientMapApiImpl;
import dev.dhyces.trimmed.impl.client.maps.MapKey;

import java.util.Map;

public interface TrimmedClientMapApi {
    static TrimmedClientMapApi getInstance() {
        return TrimmedClientMapApiImpl.INSTANCE;
    }

    <K, V> MapHolder<K, V> getSimpleMap(MapKey<K, V> key);

    <K, V, M extends Map<K, V>> MapHolder.Typed<K, V, M> getAdvancedMap(MapKey<K, V> key, AdvancedMapType<K, V, M> mapType);
}
