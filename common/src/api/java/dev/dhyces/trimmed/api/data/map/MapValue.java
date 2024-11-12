package dev.dhyces.trimmed.api.data.map;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import java.util.function.Function;

public record MapValue<V>(V value, boolean isRequired) {
    public static <V> Codec<MapValue<V>> codec(Codec<V> valueCodec) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        valueCodec.fieldOf("value").forGetter(MapValue::value),
                        Codec.BOOL.fieldOf("required").forGetter(MapValue::isRequired)
                ).apply(instance, MapValue::new)
        );
    }

    public static <V> Codec<MapValue<V>> eitherCodec(Codec<V> valueCodec) {
        return Codec.either(valueCodec, codec(valueCodec)).xmap(
                either -> either.map(s -> new MapValue<>(s, true), Function.identity()),
                mapEntry -> mapEntry.isRequired ? Either.left(mapEntry.value()) : Either.right(mapEntry)
        );
    }
}