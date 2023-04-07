package dhyces.trimmed.api.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.Trimmed;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public final class CodecUtil {
    @ApiStatus.Internal
    public static final Codec<ResourceLocation> TRIMMED_IDENTIFIER = Codec.STRING.xmap(
            s -> ResourceLocation.tryParse(s.contains(":") ? s : Trimmed.MODID + ":" + s),
            ResourceLocation::toString
    );

    public static final Codec<ModelResourceLocation> MODEL_IDENTIFIER_CODEC = Codec.STRING.comapFlatMap(
            s -> {
                if (s.contains("#")) {
                    String[] identifierModelSplit = s.split("#");
                    try {
                        ResourceLocation id = new ResourceLocation(identifierModelSplit[0]);
                        return DataResult.success(new ModelResourceLocation(id, identifierModelSplit[1]));
                    } catch (ResourceLocationException e) {
                        return DataResult.error(e::getMessage);
                    }
                }
                return DataResult.success(new ModelResourceLocation(new ResourceLocation(s), "inventory"));
            },
            modelId -> modelId.getVariant().equals("inventory") ? modelId.getNamespace() + ":" + modelId.getPath() : modelId.toString()
    );
}
