package dev.dhyces.trimmed.impl.client.maps;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public final class ClientMapKey {
    private static final Interner<ClientMapKey> INTERNER = Interners.newWeakInterner();
    public static final Codec<ClientMapKey> CODEC = ResourceLocation.CODEC.xmap(ClientMapKey::of, ClientMapKey::getMapId);
    private final ResourceLocation id;

    private ClientMapKey(ResourceLocation id) {
        this.id = id;
    }

    public static ClientMapKey of(ResourceLocation id) {
        return INTERNER.intern(new ClientMapKey(id));
    }

    public ResourceLocation getMapId() {
        return id;
    }

    @Override
    public String toString() {
        return "ClientMapKey[" + id + "]";
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ClientMapKey that = (ClientMapKey) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
