package dev.dhyces.trimmed.api.maps;

import dev.dhyces.trimmed.impl.client.maps.MapKey;

import java.util.Map;

public interface MapHolder<K, V> {
    MapKey<K, V> getKey();
    Map<K, V> getMap();
    boolean isRequired(K key);

    interface Typed<K, V, M extends Map<K, V>> extends MapHolder<K, V> {
        M getMap();
    }
}
