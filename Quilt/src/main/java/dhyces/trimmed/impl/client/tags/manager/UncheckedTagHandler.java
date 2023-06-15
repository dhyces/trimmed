package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.resources.ResourceLocation;

public final class UncheckedTagHandler extends BaseTagHandler<ClientTagKey, OptionalId> {
    @Override
    protected ClientTagKey createTag(ResourceLocation tagId) {
        return ClientTagKey.of(tagId);
    }

    @Override
    protected OptionalId createValue(OptionalId value) {
        return value;
    }
}
