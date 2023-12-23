package dev.dhyces.trimmed.impl.client.maps.manager;

import com.mojang.serialization.DataResult;
import dev.dhyces.trimmed.api.client.util.ClientUtil;
import dev.dhyces.trimmed.api.data.maps.MapValue;
import dev.dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dev.dhyces.trimmed.modhelper.services.Services;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public class RegistryMapHandler<T> extends BaseMapHandler<ClientRegistryMapKey<T>, T, String> {

    private final ResourceKey<? extends Registry<T>> registryKey;

    public RegistryMapHandler(ResourceKey<? extends Registry<T>> registryKey) {
        super(Reference2ObjectOpenHashMap::new);
        this.registryKey = registryKey;
    }

    @Override
    protected ClientRegistryMapKey<T> createMapKey(ResourceLocation mapId) {
        return ClientRegistryMapKey.of(registryKey, mapId);
    }

    @Override
    protected DataResult<T> createKey(ResourceLocation key) {
        T value = Services.PLATFORM_HELPER.getRegistryValue(ClientUtil.getRegistryAccess(), registryKey, key);
        if (value == null) {
            return DataResult.error(() -> "Could not get value for \"%s\"".formatted(key));
        }
        return DataResult.success(value);
    }

    @Override
    protected DataResult<String> parseValue(MapValue mapValue) {
        return DataResult.success(mapValue.value());
    }
}
