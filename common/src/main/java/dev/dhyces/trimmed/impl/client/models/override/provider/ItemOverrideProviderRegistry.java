package dev.dhyces.trimmed.impl.client.models.override.provider;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.resources.ResourceLocation;

public class ItemOverrideProviderRegistry {
    private static final BiMap<ResourceLocation, MapCodec<? extends ItemOverrideProvider>> PROVIDER_TYPE_MAP = HashBiMap.create();
    public static final Codec<MapCodec<? extends ItemOverrideProvider>> CODEC = CodecUtil.TRIMMED_IDENTIFIER.flatXmap(
            id -> {
                if (!PROVIDER_TYPE_MAP.containsKey(id)) {
                    return DataResult.error(() -> "Item override provider type %s does not exist!".formatted(id));
                }
                return DataResult.success(PROVIDER_TYPE_MAP.get(id));
            },
            codec -> {
                if (!PROVIDER_TYPE_MAP.inverse().containsKey(codec)) {
                    return DataResult.error(() -> "Item override provider type is not registered! " + codec);
                }
                return DataResult.success(PROVIDER_TYPE_MAP.inverse().get(codec));
            }
    );

    public static void init() {
        ItemOverrideProviders.bootstrap();
    }

    public static void register(ResourceLocation id, MapCodec<? extends ItemOverrideProvider> codec) {
        if (PROVIDER_TYPE_MAP.putIfAbsent(id, codec) != null) {
            throw new IllegalArgumentException("ItemOverrideProviderType already registered with id \"" + id + "\"");
        }
    }
}
