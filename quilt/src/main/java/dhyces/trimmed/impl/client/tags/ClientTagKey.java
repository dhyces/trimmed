package dhyces.trimmed.impl.client.tags;

import com.google.common.collect.Interner;
import com.google.common.collect.Interners;
import com.mojang.serialization.Codec;
import net.minecraft.resources.ResourceLocation;

import java.util.Objects;

public class ClientTagKey {
    private static final Interner<ClientTagKey> INTERNER = Interners.newWeakInterner();
    public static final Codec<ClientTagKey> CODEC = ResourceLocation.CODEC.xmap(ClientTagKey::of, ClientTagKey::getTagId);
    private final ResourceLocation id;

    private ClientTagKey(ResourceLocation id) {
        this.id = id;
    }

    public static ClientTagKey of(ResourceLocation tagId) {
        return INTERNER.intern(new ClientTagKey(tagId));
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
        ClientTagKey that = (ClientTagKey) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
