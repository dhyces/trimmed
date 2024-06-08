package dev.dhyces.trimmed.api.data.map.appenders;

import dev.dhyces.trimmed.api.data.map.MapBuilder;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public abstract class BaseMapAppender<K, V> {
    protected final MapBuilder<K, V> builder;

    protected <S extends BaseMapAppender<K, V>> S self() {
        return (S) this;
    }

    public BaseMapAppender(MapBuilder<K, V> builder) {
        this.builder = builder;
    }

    public <S extends BaseMapAppender<K, V>> S put(K key, V value) {
        builder.addEntry(key, value);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putOptional(K key, V value) {
        builder.addOptionalEntry(key, value);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putAll(Map<K, V> map) {
        map.forEach(builder::addEntry);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putAllOptional(Map<K, V> map) {
        map.forEach(builder::addOptionalEntry);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S append(ResourceLocation clientMapKey) {
        builder.append(clientMapKey);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S append(MapKey<K, V> mapKey) {
        builder.append(mapKey.getMapId());
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S appendOptional(ResourceLocation clientMapKey) {
        builder.appendOptional(clientMapKey);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S appendOptional(MapKey<K, V> mapKey) {
        builder.appendOptional(mapKey.getMapId());
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S replaces() {
        builder.replaces();
        return self();
    }
}
