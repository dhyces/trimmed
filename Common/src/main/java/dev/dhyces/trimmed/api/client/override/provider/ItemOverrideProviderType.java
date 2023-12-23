package dev.dhyces.trimmed.api.client.override.provider;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.client.override.provider.providers.AnyTrimItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.providers.NbtItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.providers.TrimItemOverrideProvider;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;

public interface ItemOverrideProviderType<T extends ItemOverrideProvider> {
    ItemOverrideProviderType<TrimItemOverrideProvider> ARMOR_TRIM = register("trim", TrimItemOverrideProvider.CODEC);
    ItemOverrideProviderType<NbtItemOverrideProvider> NBT = register("nbt", NbtItemOverrideProvider.CODEC);
    ItemOverrideProviderType<AnyTrimItemOverrideProvider> ANY_TRIM = register("any_trim", AnyTrimItemOverrideProvider.CODEC);

    Codec<T> getCodec();

    static void bootstrap() {}

    private static <T extends ItemOverrideProvider> ItemOverrideProviderType<T> register(String id, Codec<T> codec) {
        ItemOverrideProviderType<T> type = () -> codec;
        ItemOverrideProviderRegistry.register(Trimmed.id(id), type);
        return type;
    }
}
