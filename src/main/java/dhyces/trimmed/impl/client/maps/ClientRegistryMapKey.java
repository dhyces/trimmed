package dhyces.trimmed.impl.client.maps;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.UnboundedMapCodec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class ClientRegistryMapKey<T> {
    private static final Interner<ClientRegistryMapKey<?>> INTERNER = Interners.newWeakInterner();
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final ResourceLocation id;

    private ClientRegistryMapKey(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation id) {
        this.registryKey = registryKey;
        this.id = id;
    }

    @SuppressWarnings("unchecked")
    public static <T> ClientRegistryMapKey<T> of(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation mapId) {
        return (ClientRegistryMapKey<T>) INTERNER.intern(new ClientRegistryMapKey<>(registryKey, mapId));
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return registryKey;
    }

    public ResourceLocation getMapId() {
        return id;
    }

    public <T> Codec<ClientRegistryMapKey<T>> codec(ResourceKey<? extends Registry<T>> registryKey) {
        return ResourceLocation.CODEC.xmap(resourceLocation -> of(registryKey, resourceLocation), ClientRegistryMapKey::getMapId);
    }

    @Override
    public String toString() {
        return "ClientRegistryMapKey:[ " + registryKey.location() + " / " + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRegistryMapKey<?> clientRegistryMapKey = (ClientRegistryMapKey<?>) o;
        return Objects.equals(registryKey, clientRegistryMapKey.registryKey) && Objects.equals(id, clientRegistryMapKey.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryKey, id);
    }
}
