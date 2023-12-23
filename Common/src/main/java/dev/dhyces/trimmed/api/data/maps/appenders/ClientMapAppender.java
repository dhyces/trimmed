package dev.dhyces.trimmed.api.data.maps.appenders;

import dev.dhyces.trimmed.api.data.maps.MapBuilder;
import dev.dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public class ClientMapAppender<V> extends BaseMapAppender<ClientMapKey, V> {
    public ClientMapAppender(MapBuilder builder, Function<V, String> mappingFunction) {
        super(builder, mappingFunction);
    }

    @Override
    protected ResourceLocation keyToRL(ClientMapKey key) {
        return key.getMapId();
    }
}
