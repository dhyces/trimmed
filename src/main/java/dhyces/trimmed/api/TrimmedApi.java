package dhyces.trimmed.api;

import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.TrimmedApiImpl;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.client.tags.manager.DatapackTagHandler;
import dhyces.trimmed.impl.client.tags.manager.RegistryTagHandler;
import dhyces.trimmed.impl.client.tags.manager.UncheckedTagHandler;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;

public interface TrimmedApi {
    TrimmedApi INSTANCE = new TrimmedApiImpl();

    /**
     * This is a helper to register your own item override type. It's basically a codec supplier.
     * @param id Id for this override type, ie "trimmed:nbt"
     * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
     */
    <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType);

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

    UncheckedTagHandler getUncheckedTagHandler();

    @Nullable
    <T> RegistryTagHandler<T> getRegistryTagHandler(ResourceKey<Registry<T>> registryKey);

    @Nullable
    <T> DatapackTagHandler<T> getDatapackedTagHandler(ResourceKey<Registry<T>> registryKey);
}
