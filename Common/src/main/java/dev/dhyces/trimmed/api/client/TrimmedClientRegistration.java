package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import dev.dhyces.trimmed.api.maps.types.MapType;
import net.minecraft.resources.ResourceLocation;

public interface TrimmedClientRegistration {
    /**
     * This is a helper to register your own item override type. It's basically a codec supplier.
     * @param id Id for this override type, ie "trimmed:nbt"
     * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
     */
    <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType);
}
