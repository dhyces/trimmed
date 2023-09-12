package dhyces.trimmed.impl.mixin.client;

import net.minecraft.client.resources.model.ModelBakery;
import net.minecraft.client.resources.model.ModelResourceLocation;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(ModelBakery.class)
public interface ModelLoaderAccessor {
    @Invoker
    void invokeLoadTopLevel(ModelResourceLocation location);
}
