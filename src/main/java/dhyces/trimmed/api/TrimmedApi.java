package dhyces.trimmed.api;

import dhyces.trimmed.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.TrimmedApiImpl;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.util.Identifier;

public interface TrimmedApi {
    TrimmedApi INSTANCE = new TrimmedApiImpl();

    void registerItemOverrideType(Identifier id, ItemOverrideProviderType<?> providerType);
}
