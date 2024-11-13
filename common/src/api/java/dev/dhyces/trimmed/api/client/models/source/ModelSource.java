package dev.dhyces.trimmed.api.client.models.source;

import com.mojang.serialization.MapCodec;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;

public interface ModelSource {
    Collection<NamedModel> generate(ResourceManager resourceManager, ModelTemplateManager templateManager);
    MapCodec<? extends ModelSource> codec();
}
