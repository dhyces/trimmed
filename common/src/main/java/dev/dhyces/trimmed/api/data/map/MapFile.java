package dev.dhyces.trimmed.api.data.map;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.util.CodecUtil;

import java.util.List;
import java.util.Map;

public record MapFile<K, V>(Map<K, MapValue<V>> map, List<MapAppendElement> appendElements, boolean shouldReplace) {
    public static <K, V> Codec<MapFile<K, V>> codec(Codec<K> keyCodec, Codec<V> valueCodec) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        CodecUtil.lenientMapCodec(keyCodec, MapValue.eitherCodec(valueCodec), (kDataResult, vMapValue) -> !vMapValue.isRequired()).fieldOf("values").forGetter(MapFile::map),
                        MapAppendElement.EITHER.listOf().optionalFieldOf("append", List.of()).forGetter(MapFile::appendElements),
                        Codec.BOOL.optionalFieldOf("replace", false).forGetter(MapFile::shouldReplace)
                ).apply(instance, MapFile::new)
        );
    }
}
