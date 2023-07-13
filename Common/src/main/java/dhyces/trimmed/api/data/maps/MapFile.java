package dhyces.trimmed.api.data.maps;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record MapFile(Map<ResourceLocation, MapValue> map, List<MapAppendElement> appendElements, boolean shouldReplace) {
    public static final Codec<MapFile> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(ResourceLocation.CODEC, MapValue.EITHER_CODEC).fieldOf("pairs").forGetter(MapFile::map),
                    MapAppendElement.EITHER.listOf().optionalFieldOf("append", List.of()).forGetter(MapFile::appendElements),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(MapFile::shouldReplace)
            ).apply(instance, MapFile::new)
    );

    public static final class Builder {
        private ImmutableMap.Builder<ResourceLocation, MapValue> mapBuilder;
        private ImmutableList.Builder<MapAppendElement> appendBuilder;
        private boolean shouldReplace = false;

        public Builder() {
            mapBuilder = ImmutableMap.builder();
            appendBuilder = ImmutableList.builder();
        }

        public Builder addEntry(ResourceLocation key, String value) {
            mapBuilder.put(key, new MapValue(value, true));
            return this;
        }

        public Builder addOptionalEntry(ResourceLocation key, String value) {
            mapBuilder.put(key, new MapValue(value, false));
            return this;
        }

        public Builder addAll(Map<ResourceLocation, MapValue> map) {
            mapBuilder.putAll(map);
            return this;
        }

        public Builder append(ResourceLocation mapId) {
            appendBuilder.add(new MapAppendElement(mapId, true));
            return this;
        }

        public Builder appendOptional(ResourceLocation mapId) {
            appendBuilder.add(new MapAppendElement(mapId, false));
            return this;
        }

        public Builder appendAll(List<MapAppendElement> list) {
            appendBuilder.addAll(list);
            return this;
        }

        // Ignores "replaces"
        public Builder merge(MapFile mapFile) {
            if (!mapFile.map.isEmpty()) {
                addAll(mapFile.map);
            }
            if (!mapFile.appendElements.isEmpty()) {
                appendAll(mapFile.appendElements);
            }
            return this;
        }

        public Builder replaces() {
            shouldReplace = true;
            return this;
        }

        public MapFile build() {
            return new MapFile(mapBuilder.buildKeepingLast(), appendBuilder.build(), shouldReplace);
        }
    }
}
