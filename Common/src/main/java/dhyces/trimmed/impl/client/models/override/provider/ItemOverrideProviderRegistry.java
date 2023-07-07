package dhyces.trimmed.impl.client.models.override.provider;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.resources.ResourceLocation;

public class ItemOverrideProviderRegistry {
    private static final BiMap<ResourceLocation, ItemOverrideProviderType<?>> PROVIDER_TYPE_MAP = HashBiMap.create();
    public static final Codec<ItemOverrideProviderType<?>> CODEC = CodecUtil.TRIMMED_IDENTIFIER.comapFlatMap(
            id -> {
                if (!PROVIDER_TYPE_MAP.containsKey(id)) {
                    return DataResult.error(() -> "Item override provider type %s does not exist!".formatted(id));
                }
                return DataResult.success(PROVIDER_TYPE_MAP.get(id));
            },
            PROVIDER_TYPE_MAP.inverse()::get
    );

    public static void init() {
        ItemOverrideProviderType.bootstrap();
    }

    public static void register(ResourceLocation identifier, ItemOverrideProviderType<?> providerType) {
        PROVIDER_TYPE_MAP.put(identifier, providerType);
    }
}
