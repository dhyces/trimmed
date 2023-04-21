package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedApi;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dhyces.trimmed.impl.client.tags.manager.DatapackTagHandler;
import dhyces.trimmed.impl.client.tags.manager.RegistryTagHandler;
import dhyces.trimmed.impl.client.tags.manager.UncheckedTagHandler;
import net.minecraft.client.Minecraft;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public final class TrimmedApiImpl implements TrimmedApi {

    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }

    public boolean uncheckedTagContains(ClientTagKey tagKey, ResourceLocation value) {
        return ClientTagManager.getUncheckedHandler().doesTagContain(tagKey, value);
    }

    public <T> boolean registryTagContains(ClientRegistryTagKey<T> tagKey, T value) {
        return ClientTagManager.getRegistryHandler(tagKey.getRegistryKey()).map(handler -> handler.doesTagContain(tagKey, value)).orElse(false);
    }

    public <T> boolean datapackedTagContains(ClientRegistryTagKey<T> tagKey, Holder<T> value) {
        return ClientTagManager.getDatapackedHandler(tagKey.getRegistryKey()).map(handler -> handler.doesTagContain(tagKey, value)).orElse(false);
    }

    @Override
    public UncheckedTagHandler getUncheckedTagHandler() {
        return ClientTagManager.getUncheckedHandler();
    }

    @Override
    public <T> RegistryTagHandler<T> getRegistryTagHandler(ResourceKey<Registry<T>> registryKey) {
        return ClientTagManager.getRegistryHandler(registryKey).orElse(null);
    }

    @Override
    public <T> DatapackTagHandler<T> getDatapackedTagHandler(ResourceKey<Registry<T>> registryKey) {
        return ClientTagManager.getDatapackedHandler(registryKey).orElse(null);
    }
}