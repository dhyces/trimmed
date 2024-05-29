package dev.dhyces.trimmed.api.data.client.map.appenders;

import dev.dhyces.trimmed.api.data.map.MapBuilder;
import dev.dhyces.trimmed.api.data.map.appenders.BaseMapAppender;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;

import java.util.function.Supplier;

public class ClientRegistryMapAppender<K, V> extends BaseMapAppender<K, V> {
    private final HolderLookup.RegistryLookup<K> lookup;

    public ClientRegistryMapAppender(MapBuilder<K, V> builder, HolderLookup.RegistryLookup<K> lookup) {
        super(builder);
        this.lookup = lookup;
    }

    public <S extends ClientRegistryMapAppender<K, V>> S put(ResourceKey<K> key, V value) {
        if (!key.registry().equals(lookup.key().location())) {
            throw new IllegalArgumentException("Key " + key.location() + " is not for registry " + lookup.key() + "!");
        }
        return put(lookup.getOrThrow(key).value(), value);
    }

    public <S extends ClientRegistryMapAppender<K, V>> S putOptional(ResourceKey<K> key, V value) {
        if (!key.registry().equals(lookup.key().location())) {
            throw new IllegalArgumentException("Key " + key.location() + " is not for registry " + lookup.key() + "!");
        }
        return putOptional(lookup.getOrThrow(key).value(), value);
    }

    public ClientRegistryMapAppender<K, V> put(Supplier<K> key, V value) {
        return put(key.get(), value);
    }

    public ClientRegistryMapAppender<K, V> putOptional(Supplier<K> key, V value) {
        return putOptional(key.get(), value);
    }
}
