package dhyces.trimmed.impl.mixin;

import dhyces.trimmed.impl.client.override.ItemOverrideRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ModelOverrideList.class)
public class ModelOverrideListMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void trimmed$findModdedOverrides(BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        Optional<ModelIdentifier> optionalId = ItemOverrideRegistry.getOverrideModel(stack, world, entity, seed);
        optionalId.ifPresent(identifier -> cir.setReturnValue(MinecraftClient.getInstance().getBakedModelManager().getModel(identifier)));
    }
}
