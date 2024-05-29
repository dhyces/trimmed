package dev.dhyces.trimmed.impl.client.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class ClientTagKey<T> {
    private static final Interner<ClientTagKey<?>> INTERNER = Interners.newWeakInterner();
    public static <T> Codec<ClientTagKey<T>> codec(KeyResolver<T> keyResolver) {
        return ResourceLocation.CODEC.xmap(resourceLocation -> of(keyResolver, resourceLocation), ClientTagKey::getTagId);
    }
    public static <T> Codec<ClientTagKey<T>> tagCodec(KeyResolver<T> keyResolver) {
        return Codec.STRING.comapFlatMap(s -> (s.startsWith("#") ? ResourceLocation.read(s.substring(1)) : DataResult.<ResourceLocation>error(() -> "Not a tag id")).map(id -> of(keyResolver, id)), clientTagKey -> clientTagKey.getTagId().toString());
    }
    private final KeyResolver<T> keyResolver;
    private final ResourceLocation id;

    private ClientTagKey(KeyResolver<T> keyResolver, ResourceLocation id) {
        this.keyResolver = keyResolver;
        this.id = id;
    }

    public static <T> ClientTagKey<T> of(KeyResolver<T> keyResolver, ResourceLocation tagId) {
        return (ClientTagKey<T>) INTERNER.intern(new ClientTagKey(keyResolver, tagId));
    }

    public KeyResolver<T> getKeyResolver() {
        return keyResolver;
    }

    public ResourceLocation getTagId() {
        return id;
    }

    @Override
    public String toString() {
        return "ClientTagKey[" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientTagKey<?> that = (ClientTagKey<?>) o;
        return Objects.equals(keyResolver, that.keyResolver) && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(keyResolver, id);
    }
}
