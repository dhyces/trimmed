package dev.dhyces.trimmed.api;

import dev.dhyces.trimmed.api.maps.MapAccess;
import dev.dhyces.trimmed.impl.TrimmedClientMapApiImpl;
import dev.dhyces.trimmed.api.maps.OptionalMapEntry;
import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

public interface TrimmedClientMapApi {
    static TrimmedClientMapApi getInstance() {
        return TrimmedClientMapApiImpl.INSTANCE;
    }

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
    <T> ClientMapKeyType<T> registerKeyLoader(ResourceLocation key, Codec<T> keyCodec);
}
