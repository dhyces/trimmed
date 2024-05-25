package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedClientApi;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.resources.ResourceLocation;

public final class TrimmedClientApiImpl implements TrimmedClientApi {
    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }
}