package dev.dhyces.trimmed.api.data.maps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.util.CodecUtil;
import dev.dhyces.trimmed.api.util.LenientUnboundedMapCodec;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record MapFile<K, V>(Map<K, MapValue<V>> map, List<MapAppendElement> appendElements, boolean shouldReplace) {
    public static <K, V> Codec<MapFile<K, V>> codec(Codec<K> keyCodec, MapCodec<V> mapCodec) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        CodecUtil.lenientMapCodec(keyCodec, MapValue.eitherCodec(mapCodec), (kDataResult, vMapValue) -> !vMapValue.isRequired()).fieldOf("values").forGetter(MapFile::map),
                        MapAppendElement.EITHER.listOf().optionalFieldOf("append", List.of()).forGetter(MapFile::appendElements),
                        Codec.BOOL.optionalFieldOf("replace", false).forGetter(MapFile::shouldReplace)
                ).apply(instance, MapFile::new)
        );
    }

    public static final class Builder<K, V> {
        private ImmutableMap.Builder<K, MapValue<V>> mapBuilder;
        private ImmutableList.Builder<MapAppendElement> appendBuilder;
        private boolean shouldReplace = false;

        public Builder() {
            mapBuilder = ImmutableMap.builder();
            appendBuilder = ImmutableList.builder();
        }

        public Builder<K, V> addEntry(K key, V value) {
            mapBuilder.put(key, new MapValue<>(value, true));
            return this;
        }

        public Builder<K, V> addOptionalEntry(K key, V value) {
            mapBuilder.put(key, new MapValue<>(value, false));
            return this;
        }

        public Builder<K, V> addAll(Map<K, MapValue<V>> map) {
            mapBuilder.putAll(map);
            return this;
        }

        public Builder<K, V> append(ResourceLocation mapId) {
            appendBuilder.add(new MapAppendElement(mapId, true));
            return this;
        }

        public Builder<K, V> appendOptional(ResourceLocation mapId) {
            appendBuilder.add(new MapAppendElement(mapId, false));
            return this;
        }

        public Builder<K, V> appendAll(List<MapAppendElement> list) {
            appendBuilder.addAll(list);
            return this;
        }

        // Ignores "replaces"
        public Builder<K, V> merge(MapFile<K, V> mapFile) {
            if (!mapFile.map.isEmpty()) {
                addAll(mapFile.map);
            }
            if (!mapFile.appendElements.isEmpty()) {
                appendAll(mapFile.appendElements);
            }
            return this;
        }

        public Builder<K, V> replaces() {
            shouldReplace = true;
            return this;
        }

        public MapFile<K, V> build() {
            return new MapFile<>(mapBuilder.buildKeepingLast(), appendBuilder.build(), shouldReplace);
        }
    }
}
