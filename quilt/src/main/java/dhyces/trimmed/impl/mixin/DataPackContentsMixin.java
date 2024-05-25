package dhyces.trimmed.impl.mixin;

import net.minecraft.server.ReloadableServerResources;
import org.spongepowered.asm.mixin.Mixin;

/**
 * Used for server-sided maps, should that be something I want to do. I would need to mixin to where the listeners are
 * obtained and wrap it to return a new list with the map manager as the first reloader
 */
@Mixin(ReloadableServerResources.class)
public class DataPackContentsMixin {
}
