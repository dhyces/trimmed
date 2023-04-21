package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;

import javax.annotation.Nullable;
import java.util.Set;
import java.util.stream.Stream;

public final class UncheckedTagHandler extends BaseTagHandler<ClientTagKey, ResourceLocation, ResourceLocation> {
    @Override
    public boolean contains(ClientTagKey tag) {
        return registeredTags.containsKey(tag);
    }

    @Override
    public boolean doesTagContain(ClientTagKey tag, ResourceLocation value) {
        return registeredTags.getOrDefault(tag, Set.of()).contains(value);
    }

    @Nullable
    @Override
    public Stream<ResourceLocation> streamValues(ClientTagKey tag) {
        return registeredTags.getOrDefault(tag, Set.of()).stream();
    }

    @Override
    public Set<ResourceLocation> getSet(ClientTagKey tag) {
        return registeredTags.get(tag);
    }

    @Override
    protected ClientTagKey createTag(ResourceLocation tagId) {
        return ClientTagKey.of(tagId);
    }

    @Override
    protected ResourceLocation createValue(ResourceLocation valueId) {
        return valueId;
    }
}
