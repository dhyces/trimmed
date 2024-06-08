package dev.dhyces.trimmed.api.data.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public final class MapBuilder<K, V> {
    private ImmutableMap.Builder<K, MapValue<V>> mapBuilder;
    private ImmutableList.Builder<MapAppendElement> appendBuilder;
    private boolean shouldReplace = false;

    public MapBuilder() {
        mapBuilder = ImmutableMap.builder();
        appendBuilder = ImmutableList.builder();
    }

    public MapBuilder<K, V> addEntry(K key, V value) {
        mapBuilder.put(key, new MapValue<>(value, true));
        return this;
    }

    public MapBuilder<K, V> addOptionalEntry(K key, V value) {
        mapBuilder.put(key, new MapValue<>(value, false));
        return this;
    }

    public MapBuilder<K, V> addAll(Map<K, MapValue<V>> map) {
        mapBuilder.putAll(map);
        return this;
    }

    public MapBuilder<K, V> append(ResourceLocation mapId) {
        appendBuilder.add(new MapAppendElement(mapId, true));
        return this;
    }

    public MapBuilder<K, V> appendOptional(ResourceLocation mapId) {
        appendBuilder.add(new MapAppendElement(mapId, false));
        return this;
    }

    public MapBuilder<K, V> appendAll(List<MapAppendElement> list) {
        appendBuilder.addAll(list);
        return this;
    }

    // Ignores "replaces"
    public MapBuilder<K, V> merge(MapFile<K, V> mapFile) {
        if (!mapFile.map().isEmpty()) {
            addAll(mapFile.map());
        }
        if (!mapFile.appendElements().isEmpty()) {
            appendAll(mapFile.appendElements());
        }
        return this;
    }

    public MapBuilder<K, V> replaces() {
        shouldReplace = true;
        return this;
    }

    public MapFile<K, V> build() {
        return new MapFile<>(mapBuilder.buildKeepingLast(), appendBuilder.build(), shouldReplace);
    }
}
