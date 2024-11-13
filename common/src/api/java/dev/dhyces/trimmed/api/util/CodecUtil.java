package dev.dhyces.trimmed.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.models.source.ModelSource;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.codec.SetCodec;
import dev.dhyces.trimmed.api.services.ApiServices;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public interface CodecUtil {
    Codec<ResourceLocation> TRIMMED_IDENTIFIER = Codec.STRING.xmap(
            s -> ResourceLocation.tryParse(s.contains(":") ? s : TrimmedReference.MODID + ":" + s),
            ResourceLocation::toString
    );

    Codec<ModelResourceLocation> MODEL_IDENTIFIER_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                if (s.contains("#")) {
                    String[] identifierModelSplit = s.split("#");
                    try {
                        ResourceLocation id = ResourceLocation.parse(identifierModelSplit[0]);
                        return DataResult.success(new ModelResourceLocation(id, identifierModelSplit[1]));
                    } catch (Exception e) {
                        return DataResult.error(e::getMessage);
                    }
                }
                return DataResult.success(new ModelResourceLocation(ResourceLocation.parse(s), "inventory"));
            },
            modelId -> modelId.getVariant().equals("inventory") ? modelId.id().getNamespace() + ":" + modelId.id().getPath() : modelId.toString()
    );

    Codec<ModelSource> MODEL_SOURCE_REGISTRY = ApiServices.INTERNAL_CODECS.getModelSourceRegistryCodec();

    Codec<MapCodec<? extends ItemOverrideProvider>> ITEM_OVERRIDE_PROVIDER_REGISTRY = ApiServices.INTERNAL_CODECS.getItemOverrideProviderRegistryCodec();

    static <T> SetCodec<T> setOf(Codec<T> elementCodec) {
        return new SetCodec<>(elementCodec);
    }
}
