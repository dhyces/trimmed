package dev.dhyces.trimmed.api;

import dev.dhyces.trimmed.impl.TrimmedClientTagApiImpl;
import dev.dhyces.trimmed.impl.util.OptionalId;
import dev.dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface TrimmedClientTagApi {
    TrimmedClientTagApi INSTANCE = new TrimmedClientTagApiImpl();

    /**
     * Convenience method to test unchecked tags
     */
    boolean uncheckedTagContains(ClientTagKey tagKey, ResourceLocation value);

    /**
     * Convenience method to test registry tags
     */
    <T> boolean registryTagContains(ClientRegistryTagKey<T> tagKey, T value);

    /**
     * Convenience method to test datapacked tags
     */
    @ApiStatus.Experimental
    <T> boolean datapackedTagContains(ClientRegistryTagKey<T> tagKey, Holder<T> value);

    Set<OptionalId> getUncheckedTag(ClientTagKey clientTagKey);

    @Nullable
    <T> Set<T> getRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey);

    @ApiStatus.Experimental
    @Nullable
    <T> Set<Holder<T>> getDatapackedTag(ClientRegistryTagKey<T> clientRegistryTagKey);

    Optional<Set<OptionalId>> getSafeUncheckedTag(ClientTagKey clientTagKey);

    <T> Optional<Set<T>> getSafeRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey);

    @ApiStatus.Experimental
    <T> Optional<Set<Holder<T>>> getSafeDatapackedTag(ClientRegistryTagKey<T> clientRegistryTagKey);
}
