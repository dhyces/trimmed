package dev.dhyces.trimmed.impl.client.models.source;

import dev.dhyces.trimmed.impl.client.models.template.Template;
import net.minecraft.client.resources.model.UnbakedModel;

public interface ModelSource {
    UnbakedModel generate(Template template);
}
