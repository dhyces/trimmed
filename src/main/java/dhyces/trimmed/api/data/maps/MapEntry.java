package dhyces.trimmed.api.data.maps;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

public record MapEntry(String value, boolean optional) {
    public static final Codec<MapEntry> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.STRING.fieldOf("value").forGetter(MapEntry::value),
                    Codec.BOOL.fieldOf("required").forGetter(MapEntry::optional)
            ).apply(instance, MapEntry::new)
    );
    public static final Codec<MapEntry> EITHER_CODEC = Codec.either(Codec.STRING, CODEC).xmap(
            either -> either.map(s -> new MapEntry(s, false), mapEntry -> mapEntry),
            mapEntry -> !mapEntry.optional ? Either.left(mapEntry.value()) : Either.right(mapEntry)
    );
}