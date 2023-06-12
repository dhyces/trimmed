package dhyces.trimmed.impl.mixin.client;

import dhyces.modhelper.services.Services;
import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.client.util.ClientUtil;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.util.Utils;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Group;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(ArmorTrim.class)
public abstract class ArmorTrimMixin {

    @Shadow public abstract Holder<TrimMaterial> material();

    @Group(name = "trimmed$injectMapLogicInner", min = 1, max = 1)
    @Inject(method = "method_48432", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
    private void trimmed$injectMapLogicInnerIntermediary(Holder<TrimPattern> pattern, ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        injectMapLogic(pattern, material(), armorMaterial, "trims/models/armor/%s_leggings_%s").ifPresent(cir::setReturnValue);
    }

    @Group(name = "trimmed$injectMapLogicInner", min = 1, max = 1)
    @Inject(method = "lambda$new$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
    private void trimmed$injectMapLogicInnerSRG(Holder<TrimPattern> pattern, ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        injectMapLogic(pattern, material(), armorMaterial, "trims/models/armor/%s_leggings_%s").ifPresent(cir::setReturnValue);
    }

    @Group(name = "trimmed$injectMapLogicOuter", min = 1, max = 1)
    @Inject(method = "method_48435", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
    private void trimmed$injectMapLogicOuterIntermediary(Holder<TrimPattern> pattern, ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        injectMapLogic(pattern, material(), armorMaterial, "trims/models/armor/%s_%s").ifPresent(cir::setReturnValue);
    }

    @Group(name = "trimmed$injectMapLogicOuter", min = 1, max = 1)
    @Inject(method = "lambda$new$4", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
    private void trimmed$injectMapLogicOuterSRG(Holder<TrimPattern> pattern, ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
        injectMapLogic(pattern, material(), armorMaterial, "trims/models/armor/%s_%s").ifPresent(cir::setReturnValue);
    }

    private static Optional<ResourceLocation> injectMapLogic(Holder<TrimPattern> pattern, Holder<TrimMaterial> material, ArmorMaterial armorMaterial, String pathToFormat) {
        if (Services.PLATFORM_HELPER.isClientDist()) {
            // If a mod has an armor material for titanium, we want to check if they have a texture override for it
            // in this map, like "titanium_darker". Otherwise, we get a namespaced texture path like
            // "mymod:trims/models/armor/mypattern/someothermod-kelp". If that texture does not exist in the trim atlas,
            // then we default to vanilla behavior. Preferably, the default should only happen for vanilla trim packs;
            // mods should utilize namespaced materials in their textures.
            return TrimmedClientMapApi.INSTANCE.map(UncheckedClientMaps.ARMOR_MATERIAL_SUFFIX_OVERRIDES)
                    .getAndMap(new ResourceLocation(armorMaterial.getName()), MapValue::value)
                    .or(() -> material.unwrapKey().map(key -> Utils.namespacedPath(key.location(), '-')))
                    .map(suffix -> {
                        ResourceLocation trimTextureId = pattern.value().assetId().withPath(patternName -> pathToFormat.formatted(patternName, suffix));
                        if (!ClientUtil.trimTextureExists(trimTextureId)) {
                            return null;
                        }
                        return trimTextureId;
                    });
        }
        return Optional.empty();
    }
}