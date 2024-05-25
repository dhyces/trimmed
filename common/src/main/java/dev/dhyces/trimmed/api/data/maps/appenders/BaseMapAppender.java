package dev.dhyces.trimmed.api.data.maps.appenders;

import dev.dhyces.trimmed.api.data.maps.MapBuilder;
import dev.dhyces.trimmed.impl.client.maps.MapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Function;

public abstract class BaseMapAppender<K, V> {
    protected final MapBuilder<K, V> builder;

    protected <S extends BaseMapAppender<K, V>> S self() {
        return (S) this;
    }

    public BaseMapAppender(MapBuilder<K, V> builder) {
        this.builder = builder;
    }

    public <S extends BaseMapAppender<K, V>> S put(K key, V value) {
        builder.put(key, value);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putOptional(K key, V value) {
        builder.putOptional(key, value);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putAll(Map<K, V> map) {
        map.forEach(builder::put);
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S putAllOptional(Map<K, V> map) {
        map.forEach(builder::putOptional);
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S append(ResourceLocation clientMapKey) {
        builder.append(clientMapKey);
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S append(MapKey<K, V> mapKey) {
        builder.append(mapKey.getMapId());
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S appendOptional(ResourceLocation clientMapKey) {
        builder.appendOptional(clientMapKey);
        return self();
    }

    @Deprecated
    public <S extends BaseMapAppender<K, V>> S appendOptional(MapKey<K, V> mapKey) {
        builder.appendOptional(mapKey.getMapId());
        return self();
    }

    public <S extends BaseMapAppender<K, V>> S replaces() {
        builder.setReplace(true);
        return self();
    }
}
