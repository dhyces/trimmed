package dev.dhyces.trimmed.api.data.map.appenders;

import dev.dhyces.trimmed.api.data.map.MapBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;

public class MappedMapAppender<K, V> extends MapAppender<K, V> {
    protected final Function<K, @Nullable ResourceLocation> encoder;

    public MappedMapAppender(MapBuilder<V> builder, Function<K, @Nullable ResourceLocation> encoder) {
        super(builder);
        this.encoder = encoder;
    }

    public <S extends MapAppender<K, V>> S put(K key, V value) {
        ResourceLocation encoded = encoder.apply(key);
        if (encoded == null) {
            throw new IllegalArgumentException("Encoder could not map key to resource location");
        }
        return put(encoded, value);
    }

    public <S extends MapAppender<K, V>> S putOptional(K key, V value) {
        ResourceLocation encoded = encoder.apply(key);
        if (encoded == null) {
            throw new IllegalArgumentException("Encoder could not map key to resource location");
        }
        return putOptional(encoded, value);
    }

    public <S extends MapAppender<K, V>> S intrusivePutAll(Map<K, V> map) {
        map.forEach(this::put);
        return self();
    }

    public <S extends MapAppender<K, V>> S intrusivePutAllOptional(Map<K, V> map) {
        map.forEach(this::putOptional);
        return self();
    }
}
