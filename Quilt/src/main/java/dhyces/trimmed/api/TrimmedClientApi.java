package dhyces.trimmed.api;

import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.TrimmedClientApiImpl;
import net.minecraft.resources.ResourceLocation;

public interface TrimmedClientApi {
    TrimmedClientApi INSTANCE = new TrimmedClientApiImpl();

    /**
     * This is a helper to register your own item override type. It's basically a codec supplier.
     * @param id Id for this override type, ie "trimmed:nbt"
     * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
     */
    <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType);
}
