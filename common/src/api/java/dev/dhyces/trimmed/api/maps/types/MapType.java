package dev.dhyces.trimmed.api.maps.types;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.KeyResolver;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Reference2ObjectLinkedOpenHashMap;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.function.Supplier;

// TODO: Add optional validator
public sealed interface MapType<K, V> permits SimpleMapType, AdvancedMapType {
    KeyResolver<K> getKeyResolver();
    Codec<V> getValueCodec();
    @Nullable
    StreamCodec<RegistryFriendlyByteBuf, V> getValueStreamCodec();
    boolean isDataPackSynced();

    default Map<K, V> createMap() {
        if (getKeyResolver() instanceof KeyResolver.RegistryResolver<K>) {
            return new Reference2ObjectLinkedOpenHashMap<>();
        } else {
            return new Object2ObjectLinkedOpenHashMap<>();
        }
    }

    static <K, V> SimpleMapType<K, V> simple(KeyResolver<K> keyResolver, Codec<V> valueCodec) {
        return new SimpleMapType<>(keyResolver, valueCodec, null, false);
    }

    static <K, V> SimpleMapType.Builder<K, V> simpleBuilder(KeyResolver<K> keyResolver, Codec<V> valueCodec) {
        return SimpleMapType.builder(keyResolver, valueCodec);
    }

    static <K, V, M extends Map<K, V>> AdvancedMapType<K, V, M> advancedCollection(KeyResolver<K> keyResolver, Codec<V> valueCodec, Supplier<M> mapSupplier) {
        return new AdvancedMapType<>(keyResolver, valueCodec, null, false, mapSupplier);
    }

    static <K, V, M extends Map<K, V>> AdvancedMapType.Builder<K, V, M> advancedBuilder(KeyResolver<K> keyResolver, Codec<V> valueCodec) {
        return AdvancedMapType.builder(keyResolver, valueCodec);
    }

    abstract class BaseBuilder<K, V> {
        protected final KeyResolver<K> keyResolver;
        protected final Codec<V> valueCodec;
        @Nullable
        protected StreamCodec<RegistryFriendlyByteBuf, V> valueStreamCodec;
        protected boolean dataPackSynced = false;

        protected BaseBuilder(KeyResolver<K> keyResolver, Codec<V> valueCodec) {
            this.keyResolver = keyResolver;
            this.valueCodec = valueCodec;
        }

        /**
         * For asset pack maps, this will delay parsing of the maps until after data packs are synced to the client.
         * For data pack maps, use {@link BaseBuilder#networkCodec(StreamCodec)}.
         * @return this builder
         */
        public BaseBuilder<K, V> dataPackSynced() {
            this.dataPackSynced = true;
            return this;
        }

        /**
         * For data pack maps, this will setup syncing to clients. This is unused for asset pack maps.
         * @return this builder
         */
        public BaseBuilder<K, V> networkCodec(StreamCodec<RegistryFriendlyByteBuf, V> valueCodec) {
            this.valueStreamCodec = valueCodec;
            return dataPackSynced();
        }

        /**
         * For data pack maps, this will setup syncing to clients using the regular codecs for networking. This is
         * unused for asset pack maps.
         * @return this builder
         */
        public BaseBuilder<K, V> networked() {
            this.valueStreamCodec = ByteBufCodecs.fromCodecWithRegistries(valueCodec);
            return dataPackSynced();
        }

        // Add collector codecs and allow the maps to be built with them. Would require even more generics! Maybe
        // make AdvancedMapType...

        public abstract <T extends MapType<K, V>> T build();
    }
}
