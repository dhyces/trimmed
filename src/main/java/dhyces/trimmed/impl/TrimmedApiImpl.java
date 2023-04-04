package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.client.override.provider.ItemOverrideProviderType;
import net.minecraft.util.Identifier;

public final class TrimmedApiImpl implements TrimmedApi {

    @Override
    public void registerItemOverrideType(Identifier id, ItemOverrideProviderType<?> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
    }
}
