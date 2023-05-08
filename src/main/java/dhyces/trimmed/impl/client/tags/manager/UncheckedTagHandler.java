package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;

public final class UncheckedTagHandler extends BaseTagHandler<ClientTagKey, ResourceLocation> {
    @Override
    protected ClientTagKey createTag(ResourceLocation tagId) {
        return ClientTagKey.of(tagId);
    }

    @Override
    protected ResourceLocation createValue(ResourceLocation valueId) {
        return valueId;
    }
}
