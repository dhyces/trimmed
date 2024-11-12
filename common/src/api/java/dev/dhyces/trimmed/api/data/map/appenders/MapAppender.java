package dev.dhyces.trimmed.api.data.map.appenders;

import dev.dhyces.trimmed.api.data.map.MapBuilder;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public class MapAppender<K, V> {
    protected final MapBuilder<V> builder;

    protected <S extends MapAppender<K, V>> S self() {
        return (S) this;
    }

    public MapAppender(MapBuilder<V> builder) {
        this.builder = builder;
    }

    public <S extends MapAppender<K, V>> S put(ResourceLocation key, V value) {
        builder.addEntry(key, value);
        return self();
    }

    public <S extends MapAppender<K, V>> S putOptional(ResourceLocation key, V value) {
        builder.addOptionalEntry(key, value);
        return self();
    }

    public <S extends MapAppender<K, V>> S putAll(Map<ResourceLocation, V> map) {
        map.forEach(builder::addEntry);
        return self();
    }

    public <S extends MapAppender<K, V>> S putAllOptional(Map<ResourceLocation, V> map) {
        map.forEach(builder::addOptionalEntry);
        return self();
    }

    public <S extends MapAppender<K, V>> S append(ResourceLocation clientMapKey) {
        builder.append(clientMapKey);
        return self();
    }

    public <S extends MapAppender<K, V>> S append(MapKey<K, V> mapKey) {
        builder.append(mapKey.getMapId());
        return self();
    }

    public <S extends MapAppender<K, V>> S appendOptional(ResourceLocation clientMapKey) {
        builder.appendOptional(clientMapKey);
        return self();
    }

    public <S extends MapAppender<K, V>> S appendOptional(MapKey<K, V> mapKey) {
        builder.appendOptional(mapKey.getMapId());
        return self();
    }

    public <S extends MapAppender<K, V>> S replaces() {
        builder.replaces();
        return self();
    }
}
