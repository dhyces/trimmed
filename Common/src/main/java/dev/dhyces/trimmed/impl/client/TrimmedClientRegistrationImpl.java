package dev.dhyces.trimmed.impl.client;

import dev.dhyces.trimmed.api.client.TrimmedClientRegistration;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import dev.dhyces.trimmed.api.maps.types.MapType;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.resources.ResourceLocation;

public final class TrimmedClientRegistrationImpl implements TrimmedClientRegistration {
    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }
}
