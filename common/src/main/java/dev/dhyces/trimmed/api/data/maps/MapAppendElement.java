package dev.dhyces.trimmed.api.data.maps;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public record MapAppendElement(ResourceLocation mapId, boolean isRequired) {
    public static final Codec<MapAppendElement> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("map").forGetter(MapAppendElement::mapId),
                    Codec.BOOL.fieldOf("required").forGetter(MapAppendElement::isRequired)
            ).apply(instance, MapAppendElement::new)
    );
    public static final Codec<MapAppendElement> EITHER = Codec.either(ResourceLocation.CODEC, CODEC).xmap(
            either -> either.map(resourceLocation -> new MapAppendElement(resourceLocation, true), Function.identity()),
            mapAppendElement -> mapAppendElement.isRequired ? Either.left(mapAppendElement.mapId) : Either.right(mapAppendElement)
    );
}
