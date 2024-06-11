package dev.dhyces.trimmed.api.data.map;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public final class MapBuilder<V> {
    private ImmutableMap.Builder<ResourceLocation, MapValue<V>> mapBuilder;
    private ImmutableList.Builder<MapAppendElement> appendBuilder;
    private boolean shouldReplace = false;

    public MapBuilder() {
        mapBuilder = ImmutableMap.builder();
        appendBuilder = ImmutableList.builder();
    }

    public MapBuilder<V> addEntry(ResourceLocation key, V value) {
        mapBuilder.put(key, new MapValue<>(value, true));
        return this;
    }

    public MapBuilder<V> addOptionalEntry(ResourceLocation key, V value) {
        mapBuilder.put(key, new MapValue<>(value, false));
        return this;
    }

    public MapBuilder<V> addAll(Map<ResourceLocation, MapValue<V>> map) {
        mapBuilder.putAll(map);
        return this;
    }

    public MapBuilder<V> append(ResourceLocation mapId) {
        appendBuilder.add(new MapAppendElement(mapId, true));
        return this;
    }

    public MapBuilder<V> appendOptional(ResourceLocation mapId) {
        appendBuilder.add(new MapAppendElement(mapId, false));
        return this;
    }

    public MapBuilder<V> appendAll(List<MapAppendElement> list) {
        appendBuilder.addAll(list);
        return this;
    }

    // Ignores "replaces"
    public MapBuilder<V> merge(MapFile<V> mapFile) {
        if (!mapFile.map().isEmpty()) {
            addAll(mapFile.map());
        }
        if (!mapFile.appendElements().isEmpty()) {
            appendAll(mapFile.appendElements());
        }
        return this;
    }

    public MapBuilder<V> replaces() {
        shouldReplace = true;
        return this;
    }

    public MapFile<V> build() {
        return new MapFile<>(mapBuilder.buildKeepingLast(), appendBuilder.build(), shouldReplace);
    }
}
