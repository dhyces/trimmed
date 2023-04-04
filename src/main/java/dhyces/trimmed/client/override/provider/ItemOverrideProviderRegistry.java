package dhyces.trimmed.client.override.provider;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import dhyces.trimmed.util.CodecUtil;
import net.minecraft.util.Identifier;

public class ItemOverrideProviderRegistry {
    private static final BiMap<Identifier, ItemOverrideProviderType<?>> PROVIDER_TYPE_MAP = HashBiMap.create();
    public static final Codec<ItemOverrideProviderType<?>> CODEC = CodecUtil.TRIMMED_IDENTIFIER.xmap(PROVIDER_TYPE_MAP::get, PROVIDER_TYPE_MAP.inverse()::get);

    public static void init() {
        ItemOverrideProviderType.bootstrap();
    }

    public static void register(Identifier identifier, ItemOverrideProviderType<?> providerType) {
        PROVIDER_TYPE_MAP.put(identifier, providerType);
    }
}
