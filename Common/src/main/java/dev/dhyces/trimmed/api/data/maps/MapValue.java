package dev.dhyces.trimmed.api.data.maps;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Function;

public record MapValue(JsonElement value, boolean isRequired) {
    public static final Codec<MapValue> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.JSON.fieldOf("value").forGetter(MapValue::value),
                    Codec.BOOL.fieldOf("required").forGetter(MapValue::isRequired)
            ).apply(instance, MapValue::new)
    );
    public static final Codec<MapValue> EITHER_CODEC = Codec.either(ExtraCodecs.JSON, CODEC).xmap(
            either -> either.map(s -> new MapValue(s, true), Function.identity()),
            mapEntry -> mapEntry.isRequired ? Either.left(mapEntry.value()) : Either.right(mapEntry)
    );
}