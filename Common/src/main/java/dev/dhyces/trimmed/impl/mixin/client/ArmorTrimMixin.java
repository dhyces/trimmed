package dev.dhyces.trimmed.impl.mixin.client;

import dev.dhyces.trimmed.api.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.client.UncheckedClientMaps;
import dev.dhyces.trimmed.modhelper.services.Services;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {

    @Shadow public abstract Holder<TrimMaterial> material();

    @Inject(method = "getColorPaletteSuffix", at = @At("HEAD"), cancellable = true)
    private void trimmed$injectMapLogic(ArmorMaterial armorMaterial, CallbackInfoReturnable<String> cir) {
        if (Services.PLATFORM_HELPER.isClientDist()) {
            // If a mod has an armor material for titanium, we want to check if they have a texture override for it
            // in this map, like "titanium_darker".
            ResourceKey<TrimMaterial> trimMaterialKey = material().unwrapKey().get();
            TrimmedClientMapApi.INSTANCE.map(UncheckedClientMaps.armorMaterialOverride(trimMaterialKey))
                    .getOptional(new ResourceLocation(armorMaterial.getName()))
                    .ifPresent(cir::setReturnValue);
        }
    }
}