package dev.dhyces.trimmed.api;

import dev.dhyces.trimmed.impl.TrimmedClientApiImpl;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.util.Optional;

public interface TrimmedClientApi {
    TrimmedClientApi INSTANCE = new TrimmedClientApiImpl();

    /**
     * This is a helper to register your own item override type. It's basically a codec supplier.
     * @param id Id for this override type, ie "trimmed:nbt"
     * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
     */
    <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType);


    Optional<String> getArmorTrimSuffix(RegistryAccess registryAccess, ItemStack stack);
}
