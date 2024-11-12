package dev.dhyces.trimmed.api.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.Map;

public record MapFile<V>(Map<ResourceLocation, MapValue<V>> map, List<MapAppendElement> appendElements, boolean shouldReplace) {
    public static <V> Codec<MapFile<V>> codec(Codec<V> valueCodec) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        Codec.unboundedMap(ResourceLocation.CODEC, MapValue.eitherCodec(valueCodec)).fieldOf("values").forGetter(MapFile::map),
                        MapAppendElement.EITHER.listOf().optionalFieldOf("append", List.of()).forGetter(MapFile::appendElements),
                        Codec.BOOL.optionalFieldOf("replace", false).forGetter(MapFile::shouldReplace)
                ).apply(instance, MapFile::new)
        );
    }
}
