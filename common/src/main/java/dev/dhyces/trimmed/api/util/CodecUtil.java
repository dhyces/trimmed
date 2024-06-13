package dev.dhyces.trimmed.api.util;

import com.mojang.serialization.*;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.codec.SetCodec;
import net.minecraft.ResourceLocationException;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.BiPredicate;

public final class CodecUtil {
    public static final Codec<ResourceLocation> TRIMMED_IDENTIFIER = Codec.STRING.xmap(
            s -> ResourceLocation.tryParse(s.contains(":") ? s : Trimmed.MODID + ":" + s),
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

    public static <K, V> LenientUnboundedMapCodec<K, V> lenientMapCodec(Codec<K> keyCodec, Codec<V> valueCodec, BiPredicate<DataResult<K>, V> skipFunction) {
        return new LenientUnboundedMapCodec<>(keyCodec, valueCodec) {
            @Override
            public boolean shouldSkipKey(DataResult<K> keyParse, V value) {
                return skipFunction.test(keyParse, value);
            }
        };
    }

    public static <T> SetCodec<T> setOf(Codec<T> elementCodec) {
        return new SetCodec<>(elementCodec);
    }
}
