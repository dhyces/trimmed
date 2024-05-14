package dev.dhyces.trimmed.impl.client.models.source;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.client.ClientMapTypes;
import dev.dhyces.trimmed.impl.client.models.template.TemplateResolver;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;

public class TrimModelSource implements ModelSource {
    @Override
    public UnbakedModel generate(TemplateResolver templateResolver) {
        return null;
    }
//    private static final Codec<TrimModelSource> CODEC = RecordCodecBuilder.create(instance ->
//            instance.group(
//                    ResourceLocation.CODEC.fieldOf("template").forGetter(),
//                    Codec.unboundedMap(ResourceLocation.CODEC, ResourceLocation.CODEC).fieldOf("textures").forGetter(),
//                    TrimmedClientMapApi.getInstance().codecFor(ClientMapTypes.ALL_TRIM_PERMUTATIONS)
//            )
//    );
}
