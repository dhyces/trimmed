package dev.dhyces.trimmed.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.codec.SetCodec;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;

public final class CodecUtil {
    public static final Codec<ResourceLocation> TRIMMED_IDENTIFIER = Codec.STRING.xmap(
            s -> ResourceLocation.tryParse(s.contains(":") ? s : TrimmedReference.MODID + ":" + s),
            ResourceLocation::toString
    );

    public static final Codec<ModelResourceLocation> MODEL_IDENTIFIER_CODEC = Codec.STRING.comapFlatMap(
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

    public static <T> SetCodec<T> setOf(Codec<T> elementCodec) {
        return new SetCodec<>(elementCodec);
    }
}
