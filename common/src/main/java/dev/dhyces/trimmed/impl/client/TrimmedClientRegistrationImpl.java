package dev.dhyces.trimmed.impl.client;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
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
    public <T> MapKeyResolver<T> getOrRegisterMapKeyResolver(ResourceLocation id, Supplier<MapKeyResolver<T>> resolverSupplier) {
        MapKeyResolver<T> resolver;
        try {
            resolver = MapKeyResolvers.getResolver(id);
        } catch (ClassCastException e) {
            throw new IllegalArgumentException("Tried to cast " + MapKeyResolvers.getResolver(id) + " for the ID " + id);
        }
        if (resolver == null) {
            resolver = resolverSupplier.get();
            MapKeyResolvers.register(id, resolver);
        }
        return resolver;
    }

    @Override
    public <T> MapKeyResolver<T> registerMapKeyResolver(ResourceLocation id, MapKeyResolver<T> resolver) {
        MapKeyResolvers.register(id, resolver);
        return resolver;
    }

    @Override
    public <K, V> MapKey<K, V> registerBaseMapKey(MapKey<K, V> mapKey) {
        ClientMapManager.registerBaseKey(mapKey);
        return mapKey;
    }
}
