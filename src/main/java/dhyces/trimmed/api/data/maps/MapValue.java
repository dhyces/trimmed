package dhyces.trimmed.api.data.maps;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MapValue(String value, boolean isRequired) {
    public static final Codec<MapValue> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("value").forGetter(MapValue::value),
                    Codec.BOOL.fieldOf("required").forGetter(MapValue::isRequired)
            ).apply(instance, MapValue::new)
    );
    public static final Codec<MapValue> EITHER_CODEC = Codec.either(Codec.STRING, CODEC).xmap(
            either -> either.map(s -> new MapValue(s, true), mapEntry -> mapEntry),
            mapEntry -> mapEntry.isRequired ? Either.left(mapEntry.value()) : Either.right(mapEntry)
    );
}