package dhyces.trimmed.api;

import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.TrimmedApiImpl;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.util.Identifier;

public interface TrimmedApi {
    TrimmedApi INSTANCE = new TrimmedApiImpl();

    /**
     * This is a helper to register your own item override type. It's basically a codec supplier.
     * @param id Id for this override type, ie "trimmed:nbt"
     * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
     */
    <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(Identifier id, ItemOverrideProviderType<T> providerType);
}
