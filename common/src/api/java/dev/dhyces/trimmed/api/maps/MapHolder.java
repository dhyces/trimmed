package dev.dhyces.trimmed.api.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.BaseMapCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Optional;

public interface MapHolder<K, V> {
    /**
     * Used for elements where it could either be deserialized from a MapKey or an actual Map object.
     * @param baseMapCodec Base codec, must implement both BaseMapCodec and Codec
     * @return The codec which supplies de/serialization of maps to MapHolder
     * @param <K> Key object
     * @param <V> Value object
     * @param <C> Base codec
     */
    static <K, V, C extends BaseMapCodec<K, V> & Codec<Map<K, V>>> Codec<MapHolder<K, V>> fromBaseMapCodec(C baseMapCodec) {
        return baseMapCodec.xmap(MapHolder::simpleWrapper, MapHolder::getMap);
    }

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
