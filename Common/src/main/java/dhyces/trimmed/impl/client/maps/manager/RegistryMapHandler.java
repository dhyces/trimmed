package dhyces.trimmed.impl.client.maps.manager;

import dhyces.modhelper.services.Services;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryMapHandler<T> extends BaseMapHandler<ClientRegistryMapKey<T>, T> {

    private final ResourceKey<? extends Registry<T>> registryKey;

    public RegistryMapHandler(ResourceKey<? extends Registry<T>> registryKey) {
        this.registryKey = registryKey;
    }

    @Override
    protected ClientRegistryMapKey<T> createMapKey(ResourceLocation mapId) {
        return ClientRegistryMapKey.of(registryKey, mapId);
    }

    @Override
    protected T createKey(ResourceLocation key, MapValue value) {
        return Services.PLATFORM_HELPER.getRegistryValue(registryKey, key);
    }
}
