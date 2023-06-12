package dhyces.trimmed.impl.mixin;

import dhyces.modhelper.services.Services;
import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.api.util.Utils;
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

//    @Shadow public abstract Holder<TrimMaterial> material();
//
//    @Group(name = "trimmed$injectMapLogic", min = 1, max = 1)
//    @Inject(method = "method_48432", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
//    private void trimmed$injectMapLogicIntermediary(Holder<TrimPattern> pattern, ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
//        injectMapLogic(pattern, material(), armorMaterial).ifPresent(cir::setReturnValue);
//    }
//
//    @Group(name = "trimmed$injectMapLogic", min = 1, max = 1)
//    @Inject(method = "lambda$new$2", at = @At(value = "INVOKE", target = "Lnet/minecraft/resources/ResourceLocation;withPath(Ljava/util/function/UnaryOperator;)Lnet/minecraft/resources/ResourceLocation;"), cancellable = true)
//    private void trimmed$injectMapLogicSRG(Holder<TrimPattern> pattern, ArmorMaterial armorMaterial, CallbackInfoReturnable<ResourceLocation> cir) {
//        injectMapLogic(pattern, material(), armorMaterial).ifPresent(cir::setReturnValue);
//    }
//
//    private static Optional<ResourceLocation> injectMapLogic(Holder<TrimPattern> pattern, Holder<TrimMaterial> material, ArmorMaterial armorMaterial) {
//        if (Services.PLATFORM_HELPER.isClientDist()) {
//            return TrimmedClientMapApi.INSTANCE.getSafeUncheckedClientMap(UncheckedClientMaps.ARMOR_MATERIAL_SUFFIX_OVERRIDES)
//                    .map(map -> map.get(new ResourceLocation(armorMaterial.getName())))
//                    .map(MapValue::value)
//                    .or(() -> material.unwrapKey().map(key -> Utils.namespacedPath(key.location(), '-')))
//                    .map(suffix -> pattern.value().assetId().withPath(patternName -> "trims/models/armor/" + patternName + "/" + suffix));
//        }
//        return Optional.empty();
//    }
}
