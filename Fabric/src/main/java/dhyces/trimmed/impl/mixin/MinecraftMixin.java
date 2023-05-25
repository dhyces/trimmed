package dhyces.trimmed.impl.mixin;

import dhyces.trimmed.TrimmedClient;
import net.minecraft.client.Minecraft;
import net.minecraft.client.main.GameConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Minecraft.class)
public class MinecraftMixin {
    @Inject(method = "<init>", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/packs/repository/PackRepository;reload()V"))
    private void trimmed$injectListeners(GameConfig gameConfig, CallbackInfo ci) {
        TrimmedClient.injectListenersAtBeginning();
    }
}
