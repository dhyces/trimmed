package dev.dhyces.trimmed.api.maps.types;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import dev.dhyces.trimmed.impl.client.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public final class SimpleMapType<K, V> implements MapType<K, V> {
    private final MapKeyResolver<K> keyResolver;
    private final Codec<V> valueCodec;
    @Nullable
    private final StreamCodec<RegistryFriendlyByteBuf, V> valueStreamCodec;
    private final boolean dataPackSynced;

    SimpleMapType(MapKeyResolver<K> keyResolver, Codec<V> valueCodec, @Nullable StreamCodec<RegistryFriendlyByteBuf, V> valueStreamCodec, boolean dataPackSynced) {
        this.keyResolver = keyResolver;
        this.valueCodec = valueCodec;
        this.valueStreamCodec = valueStreamCodec;
        this.dataPackSynced = dataPackSynced;
    }

    public static <K, V> SimpleMapType.Builder<K, V> builder(MapKeyResolver<K> keyResolver, Codec<V> valueCodec) {
        return new SimpleMapType.Builder<>(keyResolver, valueCodec);
    }

    @Override
    public MapKeyResolver<K> getKeyResolver() {
        return keyResolver;
    }

    @Override
    public Codec<V> getValueCodec() {
        return valueCodec;
    }

    @Override
    public StreamCodec<RegistryFriendlyByteBuf, V> getValueStreamCodec() {
        return valueStreamCodec;
    }

    @Override
    public boolean isDataPackSynced() {
        return dataPackSynced;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        SimpleMapType<?, ?> that = (SimpleMapType<?, ?>) o;
        return dataPackSynced == that.dataPackSynced && Objects.equals(keyResolver, that.keyResolver) && Objects.equals(valueCodec, that.valueCodec) && Objects.equals(valueStreamCodec, that.valueStreamCodec);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyResolver, valueCodec, valueStreamCodec, dataPackSynced);
    }

    @Override
    public String toString() {
        ResourceLocation resolverId = MapKeyResolvers.getId(keyResolver);
        return "SimpleMapType[map_key_resolver: " + (resolverId == null ? "unregistered" : resolverId) + "]";
    }

    public static class Builder<K, V> extends BaseBuilder<K, V> {
        protected Builder(MapKeyResolver<K> keyResolver, Codec<V> valueCodec) {
            super(keyResolver, valueCodec);
        }

        @Override
        public SimpleMapType<K, V> build() {
            return new SimpleMapType<>(keyResolver, valueCodec, valueStreamCodec, dataPackSynced);
        }
    }
}
