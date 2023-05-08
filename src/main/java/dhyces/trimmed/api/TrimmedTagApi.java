package dhyces.trimmed.api;

import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Set;

public interface TrimmedTagApi {
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
    <T> boolean datapackedTagContains(ClientRegistryTagKey<T> tagKey, Holder<T> value);

    @Nullable
    Set<ResourceLocation> getUncheckedTag(ClientTagKey clientTagKey);

    @Nullable
    <T> Set<T> getRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey);

    @Nullable
    <T> Set<Holder<T>> getDatapackedTag(ClientRegistryTagKey<T> clientRegistryTagKey);
}
