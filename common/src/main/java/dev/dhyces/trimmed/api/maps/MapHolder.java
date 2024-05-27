package dev.dhyces.trimmed.api.maps;

import dev.dhyces.trimmed.impl.client.maps.MapKey;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface MapHolder<K, V> {
    default MapKey<K, V> unwrapKeyOrThrow() {
        return getKey().orElseThrow(() -> new IllegalStateException("No key is present for map holder"));
    }
    default Optional<MapKey<K, V>> getKey() {
        return Optional.ofNullable(unwrapKey());
    }
    @Nullable
    MapKey<K, V> unwrapKey();
    Map<K, V> getMap();
    boolean isRequired(K key);
    boolean isBound();

    interface Typed<K, V, M extends Map<K, V>> extends MapHolder<K, V> {
        M getMap();
    }

    static <K, V> MapHolder<K, V> simpleWrapper(Map<K, V> map) {
        return new MapHolder<>() {
            @Nullable
            @Override
            public MapKey<K, V> unwrapKey() {
                return null;
            }

            @Override
            public Map<K, V> getMap() {
                return map;
            }

            @Override
            public boolean isRequired(K key) {
                return true;
            }

            @Override
            public boolean isBound() {
                return true;
            }
        };
    }
}
