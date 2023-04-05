package dhyces.trimmed.client.override.provider;

import com.mojang.serialization.Codec;
import dhyces.trimmed.TrimmedClient;
import dhyces.trimmed.client.override.provider.providers.ArmorTrimItemOverrideProvider;
import dhyces.trimmed.client.override.provider.providers.NbtItemOverrideProvider;

public interface ItemOverrideProviderType<T extends ItemOverrideProvider> {
    ItemOverrideProviderType<ArmorTrimItemOverrideProvider> ARMOR_TRIM = register("armor_trim", ArmorTrimItemOverrideProvider.CODEC);
    ItemOverrideProviderType<NbtItemOverrideProvider> NBT = register("nbt", NbtItemOverrideProvider.CODEC);

    Codec<T> getCodec();

    static void bootstrap() {}

    private static <T extends ItemOverrideProvider> ItemOverrideProviderType<T> register(String id, Codec<T> codec) {
        ItemOverrideProviderType<T> type = () -> codec;
        ItemOverrideProviderRegistry.register(TrimmedClient.id(id), type);
        return type;
    }
}
