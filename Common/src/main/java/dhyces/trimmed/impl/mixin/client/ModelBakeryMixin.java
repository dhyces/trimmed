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
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.HashMap;
import java.util.Map;

@Mixin(ModelBakery.class)
public abstract class ModelBakeryMixin {

//    @Inject(method = "<init>", at = @At("TAIL"))
//    private void trimmed$setupGenerated(
//            BlockColors blockColors,
//            ProfilerFiller profilerFiller,
//            Map<ResourceLocation, BlockModel> map,
//            Map<ResourceLocation, List<ModelBakery.LoadedJson>> map2,
//            CallbackInfo ci
//    ) {
//
//    }

    @Shadow @Final @Mutable
    private Map<ResourceLocation, BlockModel> modelResources;

    @Shadow @Final private Map<ResourceLocation, UnbakedModel> unbakedCache;

    @Shadow protected abstract void loadTopLevel(ModelResourceLocation location);

    @Unique
    private boolean templatesGenerated;

    @Unique
    private Map<ResourceLocation, UnbakedModel> generatedModels;

    @Inject(method = "loadBlockModel", at = @At("HEAD"))
    private void trimmed$generateTemplates(ResourceLocation location, CallbackInfoReturnable<BlockModel> cir) {
        if (!templatesGenerated) {
            templatesGenerated = true;
            modelResources = new HashMap<>(modelResources);
            generatedModels = new HashMap<>();
            try {
                ModelTemplateManager.generateTemplates((resourceLocation, unbakedModel) -> {
                    ResourceLocation fileId = ModelBakery.MODEL_LISTER.idToFile(resourceLocation.withPrefix("item/"));
                    generatedModels.put(fileId, unbakedModel);
                    modelResources.put(fileId, unbakedModel);
                    loadTopLevel(new ModelResourceLocation(resourceLocation, "inventory"));
                });
            } catch (RuntimeException e) {
                Trimmed.LOGGER.error(e.getMessage());
            }
        }
    }
}
