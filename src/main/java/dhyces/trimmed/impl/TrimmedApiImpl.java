package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.util.Identifier;

public final class TrimmedApiImpl implements TrimmedApi {

    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(Identifier id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }
}