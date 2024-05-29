package dev.dhyces.trimmed.impl.client;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public final class TrimmedClientRegistrationImpl implements TrimmedClientApiEntrypoint.TrimmedClientRegistration {
    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }

    @Override
    public <T> KeyResolver<T> getOrRegisterKeyResolver(ResourceLocation id, Supplier<KeyResolver<T>> resolverSupplier) {
        KeyResolver<T> resolver;
        try {
            resolver = KeyResolvers.getResolver(id);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Tried to cast " + KeyResolvers.getResolver(id) + " for the ID " + id);
        }
        if (resolver == null) {
            resolver = resolverSupplier.get();
            KeyResolvers.register(id, resolver);
        }
        return resolver;
    }

    @Override
    public <T> KeyResolver<T> registerKeyResolver(ResourceLocation id, KeyResolver<T> resolver) {
        KeyResolvers.register(id, resolver);
        return resolver;
    }

    @Override
    public <K, V> MapKey<K, V> registerBaseMapKey(MapKey<K, V> mapKey) {
        ClientMapManager.registerBaseKey(mapKey);
        return mapKey;
    }
}
