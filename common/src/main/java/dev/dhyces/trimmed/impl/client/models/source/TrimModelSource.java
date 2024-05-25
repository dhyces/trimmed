package dev.dhyces.trimmed.impl.client.models.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.ClientMapTypes;
import dev.dhyces.trimmed.impl.client.maps.MapKey;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.List;

public record TrimModelSource() implements ModelSource {
//    private static final Codec<TrimModelSource> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//                    ResourceLocation.CODEC.fieldOf("template").forGetter(),
//                    Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("textures").forGetter(),
//                    MapKey.codec(ClientMapTypes.MATERIAL_SUFFIXES).fieldOf("")
//            )
//    );
    @Override
    public Collection<BlockModel> generate(ResourceManager resourceManager) {
        return List.of();
    }
}
