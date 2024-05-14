package dev.dhyces.trimmed.api.maps;

import dev.dhyces.trimmed.impl.client.maps.MapKey;

import java.util.Map;

public interface MapHolder<K, V, M extends Map<K, V>> {
    MapKey<K, V> getKey();
    M getMap();
    boolean isRequired(K key);
}
