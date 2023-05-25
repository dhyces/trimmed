package dhyces.trimmed.api;

import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.TrimmedApiImpl;
import dhyces.trimmed.impl.TrimmedMapApiImpl;
import dhyces.trimmed.impl.TrimmedTagApiImpl;
import net.minecraft.resources.ResourceLocation;

public interface TrimmedApi {
    TrimmedApi INSTANCE = new TrimmedApiImpl();
    TrimmedTagApi TAG_API = new TrimmedTagApiImpl();
    TrimmedMapApi MAP_API = new TrimmedMapApiImpl();

    /**
     * This is a helper to register your own item override type. It's basically a codec supplier.
     * @param id Id for this override type, ie "trimmed:nbt"
     * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
     */
    <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType);
}
