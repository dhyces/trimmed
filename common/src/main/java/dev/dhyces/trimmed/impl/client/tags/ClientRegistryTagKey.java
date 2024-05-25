package dev.dhyces.trimmed.impl.client.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class ClientRegistryTagKey<T> {
    private static final Interner<ClientRegistryTagKey<?>> INTERNER = Interners.newWeakInterner();
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final ResourceLocation id;

    private ClientRegistryTagKey(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation tagId) {
        this.registryKey = registryKey;
        this.id = tagId;
    }

    @SuppressWarnings("unchecked")
    public static <T> ClientRegistryTagKey<T> of(ResourceKey<? extends Registry<T>> registryKey, ResourceLocation tagId) {
        return (ClientRegistryTagKey<T>) INTERNER.intern(new ClientRegistryTagKey<>(registryKey, tagId));
    }

    public ResourceKey<? extends Registry<T>> getRegistryKey() {
        return registryKey;
    }

    public ResourceLocation getTagId() {
        return id;
    }

    public <T> Codec<ClientRegistryTagKey<T>> codec(ResourceKey<Registry<T>> registryKey) {
        return ResourceLocation.CODEC.xmap(resourceLocation -> of(registryKey, resourceLocation), ClientRegistryTagKey::getTagId);
    }

    @Override
    public String toString() {
        return "ClientRegistryTagKey[" + this.registryKey.location() + " / " + this.id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientRegistryTagKey<?> that = (ClientRegistryTagKey<?>) o;
        return Objects.equals(registryKey, that.registryKey) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(registryKey, id);
    }
}
