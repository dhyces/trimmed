package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedTagApi;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Set;

public final class TrimmedTagApiImpl implements TrimmedTagApi {
    @Override
    public boolean uncheckedTagContains(ClientTagKey tagKey, ResourceLocation value) {
        return ClientTagManager.getUncheckedHandler().doesTagContain(tagKey, value);
    }

    @Override
    public <T> boolean registryTagContains(ClientRegistryTagKey<T> tagKey, T value) {
        return ClientTagManager.getRegistryHandler(tagKey.getRegistryKey())
                .map(handler -> handler.doesTagContain(tagKey, value))
                .orElse(false);
    }

    @Override
    public <T> boolean datapackedTagContains(ClientRegistryTagKey<T> tagKey, Holder<T> value) {
        return ClientTagManager.getDatapackedHandler(tagKey.getRegistryKey())
                .map(handler -> handler.doesTagContain(tagKey, value))
                .orElse(false);
    }

    @Override
    @Nullable
    public Set<ResourceLocation> getUncheckedTag(ClientTagKey clientTagKey) {
        return ClientTagManager.getUncheckedHandler().getSet(clientTagKey);
    }

    @Override
    @Nullable
    public <T> Set<T> getRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey) {
        return ClientTagManager.getRegistryHandler(clientRegistryTagKey.getRegistryKey())
                .map(tRegistryTagHandler -> tRegistryTagHandler.getSet(clientRegistryTagKey))
                .orElse(null);
    }

    @Override
    @Nullable
    public <T> Set<Holder<T>> getDatapackedTag(ClientRegistryTagKey<T> clientRegistryTagKey) {
        return ClientTagManager.getDatapackedHandler(clientRegistryTagKey.getRegistryKey())
                .map(tDatapackTagHandler -> tDatapackTagHandler.getSet(clientRegistryTagKey))
                .orElse(null);
    }
}
