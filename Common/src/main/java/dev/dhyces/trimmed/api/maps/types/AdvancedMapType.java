package dev.dhyces.trimmed.api.maps.types;

import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.function.Supplier;

public final class AdvancedMapType<K, V, M extends Map<K, V>> implements MapType<K, V> {
    private final MapKeyResolver<K> keyResolver;
    private final MapCodec<V> valueCodec;
    @Nullable
    private final StreamCodec<RegistryFriendlyByteBuf, V> valueStreamCodec;
    private final boolean dataPackSynced;
    private final Supplier<M> mapSupplier;

    AdvancedMapType(MapKeyResolver<K> keyResolver, MapCodec<V> valueCodec, @Nullable StreamCodec<RegistryFriendlyByteBuf, V> valueStreamCodec, boolean dataPackSynced, @Nullable Supplier<M> mapSupplier) {
        this.keyResolver = keyResolver;
        this.valueCodec = valueCodec;
        this.valueStreamCodec = valueStreamCodec;
        this.dataPackSynced = dataPackSynced;
        this.mapSupplier = mapSupplier;
    }

    public static <K, V, M extends Map<K, V>> Builder<K, V, M> builder(MapKeyResolver<K> keyResolver, MapCodec<V> valueCodec) {
        return new Builder<>(keyResolver, valueCodec);
    }

    @Override
    public MapKeyResolver<K> getKeyResolver() {
        return keyResolver;
    }

    @Override
    public MapCodec<V> getValueCodec() {
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
    public Map<K, V> createMap() {
        if (mapSupplier != null) {
            return mapSupplier.get();
        }
        return MapType.super.createMap();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AdvancedMapType<?, ?, ?> that = (AdvancedMapType<?, ?, ?>) o;
        return dataPackSynced == that.dataPackSynced && Objects.equals(keyResolver, that.keyResolver) && Objects.equals(valueCodec, that.valueCodec) && Objects.equals(valueStreamCodec, that.valueStreamCodec) && Objects.equals(mapSupplier, that.mapSupplier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyResolver, valueCodec, valueStreamCodec, dataPackSynced, mapSupplier);
    }

    @Override
    public String toString() {
        ResourceLocation resolverId = MapKeyResolvers.getId(keyResolver);
        return "AdvancedMapType[map_key_resolver: " + (resolverId == null ? "unregistered" : resolverId) + "]";
    }

    public static class Builder<K, V, M extends Map<K, V>> extends BaseBuilder<K, V> {
        private Supplier<M> mapSupplier;
        protected Builder(MapKeyResolver<K> keyResolver, MapCodec<V> valueCodec) {
            super(keyResolver, valueCodec);
        }

        public Builder<K, V, M> collectInto(Supplier<M> mapSupplier) {
            this.mapSupplier = mapSupplier;
            return this;
        }

        @Override
        public AdvancedMapType<K, V, M> build() {
            return new AdvancedMapType<>(keyResolver, valueCodec, valueStreamCodec, dataPackSynced, mapSupplier);
        }
    }
}
