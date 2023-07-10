package dhyces.trimmed.impl.mixin.client;

import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.client.resources.model.UnbakedModel;
import net.minecraft.resources.ResourceLocation;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

import static net.minecraft.client.resources.model.ModelBakery.MISSING_MODEL_LOCATION;

@Mixin(ModelBakery.class)
public abstract class ModelLoaderMixin {

    @Shadow @Final @Mutable
    private Map<ResourceLocation, BlockModel> modelResources;

    @Shadow protected abstract void loadTopLevel(ModelResourceLocation location);

    @Unique
    private Map<ResourceLocation, UnbakedModel> generatedModels;

    @Inject(method = "loadTopLevel", at = @At("HEAD"))
    private void trimmed$generateTemplates(ModelResourceLocation location, CallbackInfo ci) {
        if (location == MISSING_MODEL_LOCATION) {
            modelResources = new HashMap<>(modelResources);
            generatedModels = new HashMap<>();
            try {
                ModelTemplateManager.generateTemplates((resourceLocation, modelSupplier) -> {
                    ResourceLocation fileId = ModelBakery.MODEL_LISTER.idToFile(resourceLocation.withPrefix("item/"));
                    if (!modelResources.containsKey(fileId)) {
                        BlockModel model = modelSupplier.get();
                        generatedModels.put(fileId, model);
                        modelResources.put(fileId, model);
                        loadTopLevel(new ModelResourceLocation(resourceLocation, "inventory"));
                    }
                });
            } catch (RuntimeException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }
    }
}
