package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.maps.KeyResolver;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Supplier;

public interface TrimmedClientApiEntrypoint {
    /**
     * Allows registration for all client elements, such as item model override types, map key resolvers, and base keys
     * @param registration Object with methods to register client elements
     */
    void registration(TrimmedClientRegistration registration);

    interface TrimmedClientRegistration {
        /**
         * This is a helper to register your own item override type. It's basically a codec supplier.
         * @param id Id for this override type, ie "trimmed:nbt"
         * @param providerType Your item override provider type, ie () -> MyItemOverrideProvider.CODEC
         */
        <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType);

        /**
         * If this already
         * @param id
         * @param resolverSupplier
         * @return
         * @param <T>
         */
        <T> KeyResolver<T> getOrRegisterKeyResolver(ResourceLocation id, Supplier<KeyResolver<T>> resolverSupplier);

        <T> KeyResolver<T> registerKeyResolver(ResourceLocation id, KeyResolver<T> resolver);

        <K, V> MapKey<K, V> registerBaseMapKey(MapKey<K, V> mapKey);
    }
}
