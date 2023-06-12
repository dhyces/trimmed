package dhyces.trimmed.impl.mixin;

import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import dhyces.trimmed.impl.maps.MapValueHolder;
import net.minecraft.core.Holder;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(Holder.Reference.class)
public class ReferenceMixin<T> implements MapValueHolder<T> {
    @Override
    public boolean hasValue() {
        return false;
    }

    @Override
    public <V> V getValue(ClientRegistryMapKey<T> mapKey) {
        return null;
    }
}
