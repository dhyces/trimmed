package dev.dhyces.trimmed.api.data.maps;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MapBuilder<K, V> {
    private final ImmutableMap.Builder<K, MapValue<V>> builder;
    private final List<MapAppendElement> appendedMaps;
    private boolean isReplace;

    public MapBuilder() {
        this.builder = ImmutableMap.builder();
        this.appendedMaps = new ArrayList<>();
    }

    public MapBuilder<K, V> put(K key, V val) {
        builder.put(key, new MapValue<>(val, true));
        return this;
    }

    public MapBuilder<K, V> putOptional(K key, V val) {
        builder.put(key, new MapValue<>(val, false));
        return this;
    }

    public MapBuilder<K, V> append(ResourceLocation map) {
        appendedMaps.add(new MapAppendElement(map, true));
        return this;
    }

    public MapBuilder<K, V> appendOptional(ResourceLocation map) {
        appendedMaps.add(new MapAppendElement(map, false));
        return this;
    }

    public MapBuilder<K, V> setReplace(boolean shouldReplace) {
        this.isReplace = shouldReplace;
        return this;
    }

    public MapValue<V> get(K key) {
        return builder.build().get(key);
    }

    public MapFile<K, V> build() {
        return new MapFile<>(builder.build(), appendedMaps, isReplace);
    }
}
