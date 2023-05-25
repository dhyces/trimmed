package dhyces.trimmed.impl.client.maps.manager;

import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.util.OptionalId;
import net.minecraft.resources.ResourceLocation;

public class UncheckedMapHandler extends BaseMapHandler<ClientMapKey, OptionalId> {
    @Override
    protected ClientMapKey createMapKey(ResourceLocation mapId) {
        return ClientMapKey.of(mapId);
    }

    @Override
    protected OptionalId createKey(ResourceLocation key, MapValue value) {
        return new OptionalId(key, value.isRequired());
    }
}
