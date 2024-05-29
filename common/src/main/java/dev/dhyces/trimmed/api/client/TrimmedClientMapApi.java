package dev.dhyces.trimmed.api.client;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.api.maps.types.AdvancedMapType;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.impl.client.TrimmedClientMapApiImpl;
import dev.dhyces.trimmed.api.maps.MapKey;

import java.util.Map;

public interface TrimmedClientMapApi {
    static TrimmedClientMapApi getInstance() {
        return TrimmedClientMapApiImpl.INSTANCE;
    }

    <K, V> MapHolder<K, V> getSimpleMap(MapKey<K, V> key);

    <K, V> Codec<MapHolder<K, V>> simpleCodec(MapType<K, V> mapType);

    <K, V, M extends Map<K, V>> MapHolder.Typed<K, V, M> getAdvancedMap(MapKey<K, V> key, AdvancedMapType<K, V, M> mapType);

    <K, V, M extends Map<K, V>> Codec<MapHolder.Typed<K, V, M>> advancedCodec(AdvancedMapType<K, V, M> mapType);
}
