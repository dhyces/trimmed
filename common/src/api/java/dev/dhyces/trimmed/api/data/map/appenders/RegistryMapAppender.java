package dev.dhyces.trimmed.api.data.map.appenders;

import dev.dhyces.trimmed.api.data.map.MapBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Function;
import java.util.function.Supplier;

public class RegistryMapAppender<K, V> extends MapAppender<K, V> {
    private final HolderLookup.RegistryLookup<K> lookup;

    public RegistryMapAppender(MapBuilder<V> builder, HolderLookup.RegistryLookup<K> lookup) {
        super(builder);
        this.lookup = lookup;
    }

    public <S extends RegistryMapAppender<K, V>> S put(ResourceKey<K> key, V value) {
        if (!key.isFor(lookup.key())) {
            throw new IllegalArgumentException("Key " + key.location() + " is not for registry " + lookup.key() + "!");
        }
        return put(key.location(), value);
    }

    public <S extends RegistryMapAppender<K, V>> S putOptional(ResourceKey<K> key, V value) {
        if (!key.isFor(lookup.key())) {
            throw new IllegalArgumentException("Key " + key.location() + " is not for registry " + lookup.key() + "!");
        }
        return putOptional(key.location(), value);
    }

    public RegistryMapAppender<K, V> put(Holder<K> holder, V value) {
        if (!holder.unwrapKey().orElseThrow().isFor(lookup.key())) {
            throw new IllegalArgumentException("Element " + holder + " is not valid in current registry set");
        }
        return put(holder.unwrapKey().map(ResourceKey::location).orElseThrow(), value);
    }

    public RegistryMapAppender<K, V> putOptional(Holder<K> holder, V value) {
        if (!holder.unwrapKey().orElseThrow().isFor(lookup.key())) {
            throw new IllegalArgumentException("Element " + holder + " is not valid in current registry set");
        }
        return putOptional(holder.unwrapKey().map(ResourceKey::location).orElseThrow(), value);
    }

    public static class Mapped<K, V> extends RegistryMapAppender<K, V> {
        protected final Function<K, @Nullable ResourceLocation> encoder;

        public Mapped(MapBuilder<V> builder, HolderLookup.RegistryLookup<K> lookup, Function<K, @Nullable ResourceLocation> encoder) {
            super(builder, lookup);
            this.encoder = encoder;
        }

        public <S extends MapAppender<K, V>> S put(K key, V value) {
            ResourceLocation encoded = encoder.apply(key);
            if (encoded == null) {
                throw new IllegalArgumentException("Encoder could not map key to resource location");
            }
            return put(encoded, value);
        }

        public <S extends MapAppender<K, V>> S put(Supplier<K> key, V value) {
            return put(key.get(), value);
        }

        public <S extends MapAppender<K, V>> S putOptional(K key, V value) {
            ResourceLocation encoded = encoder.apply(key);
            if (encoded == null) {
                throw new IllegalArgumentException("Encoder could not map key to resource location");
            }
            return putOptional(encoded, value);
        }

        public <S extends MapAppender<K, V>> S putOptional(Supplier<K> key, V value) {
            return putOptional(key.get(), value);
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
}
