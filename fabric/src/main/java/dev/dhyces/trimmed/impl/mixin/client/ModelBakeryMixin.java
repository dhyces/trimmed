package dev.dhyces.trimmed.impl.mixin.client;

import dev.dhyces.trimmed.impl.ModelBakeryHelper;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

import java.util.Map;

@Mixin(ModelBakery.class)
public class ModelBakeryMixin implements ModelBakeryHelper {
    @Shadow @Final private Map<ResourceLocation, BlockModel> modelResources;

    @Override
    public boolean trimmed$hasResourceFor(ResourceLocation path) {
        return modelResources.containsKey(path);
    }
}
