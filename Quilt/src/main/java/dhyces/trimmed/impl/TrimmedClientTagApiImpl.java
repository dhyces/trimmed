package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedClientTagApi;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public final class TrimmedClientTagApiImpl implements TrimmedClientTagApi {
    @Override
    public boolean uncheckedTagContains(ClientTagKey tagKey, ResourceLocation value) {
        return OptionalId.checkEither(value, optionalTagElement -> ClientTagManager.getUncheckedHandler().doesTagContain(tagKey, optionalTagElement));
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
    public Set<OptionalId> getUncheckedTag(ClientTagKey clientTagKey) {
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

    @Override
    public Optional<Set<OptionalId>> getSafeUncheckedTag(ClientTagKey clientTagKey) {
        return Optional.ofNullable(ClientTagManager.getUncheckedHandler().getSet(clientTagKey));
    }

    @Override
    public <T> Optional<Set<T>> getSafeRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey) {
        return ClientTagManager.getRegistryHandler(clientRegistryTagKey.getRegistryKey())
                .map(tRegistryTagHandler -> tRegistryTagHandler.getSet(clientRegistryTagKey));
    }

    @Override
    public <T> Optional<Set<Holder<T>>> getSafeDatapackedTag(ClientRegistryTagKey<T> clientRegistryTagKey) {
        return ClientTagManager.getDatapackedHandler(clientRegistryTagKey.getRegistryKey())
                .map(tDatapackTagHandler -> tDatapackTagHandler.getSet(clientRegistryTagKey));
    }
}
