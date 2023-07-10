package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.maps.LimitedMap;
import dhyces.trimmed.api.maps.OptionalMapEntry;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.resources.ResourceLocation;

import java.util.Iterator;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public final class TrimmedClientMapApiImpl implements TrimmedClientMapApi {
    @Override
    public LimitedMap<ResourceLocation, String> map(ClientMapKey clientMapKey) {
        return ClientMapManager.getUncheckedHandler().getMap(clientMapKey);
    }

    @Override
    public Stream<OptionalMapEntry<ResourceLocation, String>> mapStream(ClientMapKey clientMapKey) {
        return ClientMapManager.getUncheckedHandler().getHolder(clientMapKey)
                .map(holder ->
                        holder.getBacking().entrySet().stream()
                        .map(entry -> new OptionalMapEntry<>(entry.getKey(), entry.getValue(), holder.isRequired(entry.getKey()))))
                .orElse(Stream.empty());
    }

    @Override
    public <K> LimitedMap<K, String> map(ClientRegistryMapKey<K> clientRegistryMapKey) {
        return ClientMapManager.getRegistryHandler(clientRegistryMapKey.getRegistryKey()).getMap(clientRegistryMapKey);
    }

    @Override
    public String getUncheckedClientValue(ClientMapKey clientMapKey, ResourceLocation key) {
        return ClientMapManager.getUncheckedHandler().getValue(clientMapKey, key);
    }

    @Override
    public <K> String getRegistryClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, K key) {
        return ClientMapManager.getRegistryHandler(clientRegistryMapKey.getRegistryKey()).getValue(clientRegistryMapKey, key);
    }
}
