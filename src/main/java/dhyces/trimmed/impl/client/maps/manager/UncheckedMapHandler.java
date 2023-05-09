package dhyces.trimmed.impl.client.maps.manager;

import dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.resources.ResourceLocation;

public class UncheckedMapHandler extends BaseMapHandler<ClientMapKey, ResourceLocation> {
    @Override
    protected ClientMapKey createMapKey(ResourceLocation mapId) {
        return ClientMapKey.of(mapId);
    }

    @Override
    protected ResourceLocation createKey(ResourceLocation keyId) {
        return keyId;
    }
}
