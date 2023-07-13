package dhyces.trimmed.impl.client.maps.manager;

import com.mojang.serialization.DataResult;
import dhyces.trimmed.api.data.maps.MapValue;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class UncheckedMapHandler extends BaseMapHandler<ClientMapKey, ResourceLocation, String> {

    public UncheckedMapHandler() {
        super(HashMap::new);
    }

    @Override
    protected ClientMapKey createMapKey(ResourceLocation mapId) {
        return ClientMapKey.of(mapId);
    }

    @Override
    protected DataResult<ResourceLocation> createKey(ResourceLocation key) {
        return DataResult.success(key);
    }

    @Override
    protected DataResult<String> parseValue(MapValue mapValue) {
        return DataResult.success(mapValue.value());
    }
}
