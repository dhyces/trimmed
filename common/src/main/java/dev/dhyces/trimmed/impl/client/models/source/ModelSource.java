package dev.dhyces.trimmed.impl.client.models.source;

import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;

public interface ModelSource {
    Collection<NamedModel> generate(ResourceManager resourceManager, ModelTemplateManager templateManager);
    MapCodec<? extends ModelSource> codec();
}
