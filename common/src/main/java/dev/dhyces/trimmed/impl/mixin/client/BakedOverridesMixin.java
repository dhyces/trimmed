package dev.dhyces.trimmed.impl.mixin.client;

import dev.dhyces.trimmed.impl.client.models.override.ItemOverrideRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BakedOverrides;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(BakedOverrides.class)
public class BakedOverridesMixin {

    @Inject(method = "findOverride", at = @At("HEAD"), cancellable = true)
    private void trimmed$findModdedOverrides(ItemStack stack, ClientLevel level, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        Optional<BakedModel> optionalModel = ItemOverrideRegistry.getOverrideModel(stack, level, entity, seed);
        optionalModel.ifPresent(cir::setReturnValue);
    }
}
