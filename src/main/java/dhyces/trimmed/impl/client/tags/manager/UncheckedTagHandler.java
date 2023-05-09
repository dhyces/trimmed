package dhyces.trimmed.impl.client.tags.manager;

import dhyces.trimmed.impl.client.tags.ClientTagKey;
import dhyces.trimmed.impl.util.OptionalTagElement;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagEntry;

import java.util.Optional;

public final class UncheckedTagHandler extends BaseTagHandler<ClientTagKey, OptionalTagElement> {
    @Override
    protected ClientTagKey createTag(ResourceLocation tagId) {
        return ClientTagKey.of(tagId);
    }

    @Override
    protected OptionalTagElement createValue(OptionalTagElement value) {
        return value;
    }
}
