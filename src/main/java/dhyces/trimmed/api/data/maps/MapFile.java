package dhyces.trimmed.api.data.maps;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public record MapFile(Map<ResourceLocation, MapValue> map, boolean shouldReplace) {
    public static final Codec<MapFile> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Codec.unboundedMap(ResourceLocation.CODEC, MapValue.EITHER_CODEC).fieldOf("pairs").forGetter(MapFile::map),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(MapFile::shouldReplace)
            ).apply(instance, MapFile::new)
    );
}
