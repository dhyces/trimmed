package dev.dhyces.trimmed.impl.client.maps;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.maps.MapType;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class ClientMapKey<K, V> {
    public static <K, V> Codec<ClientMapKey<K, V>> codec(MapType<K, V> mapType) {
        return ResourceLocation.CODEC.xmap(resourceLocation -> ClientMapKey.of(mapType, resourceLocation), ClientMapKey::getMapId);
    }

    private static final Interner<ClientMapKey<?, ?>> INTERNER = Interners.newWeakInterner();

    private final ResourceLocation id;

    private ClientMapKey(ResourceLocation id) {
        this.id = id;
    }

    public static <K, V> ClientMapKey<K, V> of(MapType<K, V> mapType, ResourceLocation id) {
        // Doesn't store map type so that multiple sources may access same maps with different objs
        ClientMapKey<K, V> key = new ClientMapKey<>(id);
        INTERNER.intern(key);
        return key;
    }

    public ResourceLocation getMapId() {
        return id;
    }

    @Override
    public String toString() {
        return "ClientMapKey[" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientMapKey<?, ?> that = (ClientMapKey<?, ?>) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
