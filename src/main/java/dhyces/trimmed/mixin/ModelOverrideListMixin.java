package dhyces.trimmed.mixin;

import dhyces.trimmed.client.override.ItemOverrideRegistry;
import dhyces.trimmed.client.override.OverrideSet;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.render.model.json.ModelOverrideList;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;
import java.util.function.Function;

@Mixin(ModelOverrideList.class)
public class ModelOverrideListMixin {

    @Inject(method = "apply", at = @At("HEAD"), cancellable = true)
    private void trimmed$findModdedOverrides(BakedModel model, ItemStack stack, ClientWorld world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        findModdedOverrides(stack, world, entity, seed, cir);
    }

    private void findModdedOverrides(ItemStack stack, ClientWorld world, LivingEntity entity, int seed, CallbackInfoReturnable<BakedModel> cir) {
        ModelIdentifier id = MinecraftClient.getInstance().getItemRenderer().getModels().modelIds.get(Item.getRawId(stack.getItem()));
        Optional<OverrideSet> optionalSet = ItemOverrideRegistry.getOverrideSet(id);
        optionalSet.map(set -> set.testProviders(stack, world, entity, seed))
                .flatMap(Function.identity())
                .ifPresent(identifier -> cir.setReturnValue(MinecraftClient.getInstance().getBakedModelManager().getModel(identifier)));
    }
}
