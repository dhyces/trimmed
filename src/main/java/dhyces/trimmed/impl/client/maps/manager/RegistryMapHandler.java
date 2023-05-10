package dhyces.trimmed.impl.client.maps.manager;

import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryManager;

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
        return RegistryManager.ACTIVE.getRegistry(registryKey).getValue(key);
    }
}
