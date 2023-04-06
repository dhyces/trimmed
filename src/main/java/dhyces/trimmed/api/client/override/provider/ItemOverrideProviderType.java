package dhyces.trimmed.api.client.override.provider;

import com.mojang.serialization.Codec;
import dhyces.trimmed.TrimmedClient;
import dhyces.trimmed.api.client.override.provider.providers.NbtItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.TrimItemOverrideProvider;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;

public interface ItemOverrideProviderType<T extends ItemOverrideProvider> {
    ItemOverrideProviderType<TrimItemOverrideProvider> ARMOR_TRIM = register("trim", TrimItemOverrideProvider.CODEC);
    ItemOverrideProviderType<NbtItemOverrideProvider> NBT = register("nbt", NbtItemOverrideProvider.CODEC);

    Codec<T> getCodec();

    static void bootstrap() {}

    private static <T extends ItemOverrideProvider> ItemOverrideProviderType<T> register(String id, Codec<T> codec) {
        ItemOverrideProviderType<T> type = () -> codec;
        ItemOverrideProviderRegistry.register(TrimmedClient.id(id), type);
        return type;
    }
}
