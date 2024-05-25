package dev.dhyces.trimmed.impl.client.models.source;

import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;

public interface ModelSource {
    Collection<BlockModel> generate(ResourceManager resourceManager);
}
