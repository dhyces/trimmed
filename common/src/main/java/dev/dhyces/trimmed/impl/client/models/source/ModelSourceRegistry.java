package dev.dhyces.trimmed.impl.client.models.source;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.resources.ResourceLocation;

public class ModelSourceRegistry {
    private static final BiMap<ResourceLocation, MapCodec<? extends ModelSource>> REGISTRY = HashBiMap.create();
    public static final Codec<ModelSource> CODEC = CodecUtil.TRIMMED_IDENTIFIER.dispatch(modelSource -> REGISTRY.inverse().get(modelSource.codec()), REGISTRY::get);

    public static void register(ResourceLocation id, MapCodec<? extends ModelSource> mapCodec) {
        if (REGISTRY.putIfAbsent(id, mapCodec) != null) {
            throw new IllegalArgumentException("Codec already registered for " + id);
        }
    }

    public static void init() {
        register(TrimmedReference.id("trims"), TrimModelSource.CODEC);
    }
}
