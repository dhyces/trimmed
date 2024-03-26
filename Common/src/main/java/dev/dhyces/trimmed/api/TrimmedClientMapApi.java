package dev.dhyces.trimmed.api;

import dev.dhyces.trimmed.api.maps.MapAccess;
import dev.dhyces.trimmed.impl.TrimmedClientMapApiImpl;
import dev.dhyces.trimmed.api.maps.OptionalMapEntry;
import dev.dhyces.trimmed.impl.client.maps.ClientMapKey;
import dev.dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.stream.Stream;

public interface TrimmedClientMapApi {
    TrimmedClientMapApi INSTANCE = new TrimmedClientMapApiImpl();

    @ApiStatus.Experimental
    MapAccess<ResourceLocation, String> map(ClientMapKey clientMapKey);

    @ApiStatus.Experimental
    Stream<OptionalMapEntry<ResourceLocation, String>> mapStream(ClientMapKey clientMapKey);

    @ApiStatus.Experimental
    <K> MapAccess<K, String> map(ClientRegistryMapKey<K> clientRegistryMapKey);

    @Nullable
    String getUncheckedClientValue(ClientMapKey clientMapKey, ResourceLocation key);

    @Nullable
    <K> String getRegistryClientValue(ClientRegistryMapKey<K> clientRegistryMapKey, K key);
}
