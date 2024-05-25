package dhyces.trimmed.impl.mixin.client;

import dhyces.trimmed.impl.client.override.ItemOverrideRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ItemOverrides.class)
public class ModelOverrideListMixin {

    @Inject(method = "resolve", at = @At("HEAD"), cancellable = true)
    private void trimmed$findModdedOverrides(BakedModel oldModel, ItemStack stack, ClientLevel world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        Optional<BakedModel> optionalModel = ItemOverrideRegistry.getOverrideModel(stack, world, entity, seed);
        optionalModel.ifPresent(cir::setReturnValue);
    }
}
