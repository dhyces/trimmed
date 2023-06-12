package dhyces.trimmed.impl.mixin;

import com.google.common.collect.ImmutableList;
import dhyces.trimmed.impl.maps.MapManager;
import net.minecraft.commands.Commands;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.ReloadableServerResources;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.world.flag.FeatureFlagSet;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

/**
 * Used for server-sided maps, should that be something I want to do. I would need to mixin to where the listeners are
 * obtained and wrap it to return a new list with the map manager as the first reloader
 */
@Mixin(ReloadableServerResources.class)
public class DataPackContentsMixin {

    @Unique
    private MapManager mapManager;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void trimmed$initMapManager(RegistryAccess.Frozen frozen, FeatureFlagSet featureFlagSet, Commands.CommandSelection commandSelection, int i, CallbackInfo ci) {
        mapManager = new MapManager(frozen);
    }

    @Inject(method = "listeners", at = @At("TAIL"), cancellable = true)
    private void trimmed$addAsFirstListener(CallbackInfoReturnable<List<PreparableReloadListener>> cir) {
        ImmutableList.Builder<PreparableReloadListener> builder = ImmutableList.builder();
        List<PreparableReloadListener> original = cir.getReturnValue();
        builder.add(original.get(0));
        builder.add(mapManager);
        builder.addAll(original.subList(1, original.size()));
        cir.setReturnValue(builder.build());
    }

    @Inject(method = "updateRegistryTags(Lnet/minecraft/core/RegistryAccess;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/block/Blocks;rebuildCache()V"))
    private void trimmed$updateMaps(RegistryAccess registryAccess, CallbackInfo ci) {

    }
}
