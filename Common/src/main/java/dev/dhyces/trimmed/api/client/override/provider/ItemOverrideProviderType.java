package dev.dhyces.trimmed.api.client.override.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.client.override.provider.providers.AnyTrimItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.providers.ComponentItemOverrideProvider;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;

public interface ItemOverrideProviderType<T extends ItemOverrideProvider> {
    ItemOverrideProviderType<ComponentItemOverrideProvider> NBT = register("nbt", ComponentItemOverrideProvider.CODEC);
    ItemOverrideProviderType<AnyTrimItemOverrideProvider> ANY_TRIM = register("any_trim", AnyTrimItemOverrideProvider.CODEC);

    MapCodec<T> getCodec();

    static void bootstrap() {}

    private static <T extends ItemOverrideProvider> ItemOverrideProviderType<T> register(String id, MapCodec<T> codec) {
        ItemOverrideProviderType<T> type = () -> codec;
        ItemOverrideProviderRegistry.register(Trimmed.id(id), type);
        return type;
    }
}
