package dev.dhyces.trimmed.api.maps;

import com.mojang.serialization.Codec;

public final class MapType<K, V> {
    private final Codec<K> keyCodec;
    private final Codec<V> valueCodec;
    private final boolean datapackSynced;

    MapType(Codec<K> keyCodec, Codec<V> valueCodec, boolean datapackSynced) {
        this.keyCodec = keyCodec;
        this.valueCodec = valueCodec;
        this.datapackSynced = datapackSynced;
    }

    public static <K, V> Builder<K, V> builder(Codec<K> keyCodec, Codec<V> valueCodec) {
        return new Builder<>(keyCodec, valueCodec);
    }

    public static class Builder<K, V> {
        private final Codec<K> keyCodec;
        private final Codec<V> valueCodec;
        private boolean datapackSynced = false;

        private Builder(Codec<K> keyCodec, Codec<V> valueCodec) {
            this.keyCodec = keyCodec;
            this.valueCodec = valueCodec;
        }

        /**
         * For datapack maps, this will cause the entries to be synced to clients. For asset pack maps, this will delay
         * parsing of the maps until after datapacks are synced to the client.
         * @return this builder
         */
        public Builder<K, V> datapackSynced() {
            this.datapackSynced = true;
            return this;
        }

        // Add collector codecs and allow the maps to be built with them. Would require even more generics! Maybe
        // make AdvancedMapType...

        public MapType<K, V> build() {
            return new MapType<>(keyCodec, valueCodec, datapackSynced);
        }
    }
}
