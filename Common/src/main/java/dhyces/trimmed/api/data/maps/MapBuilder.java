package dhyces.trimmed.api.data.maps;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

public class MapBuilder {

    private final ImmutableMap.Builder<ResourceLocation, MapValue> builder;
    private boolean isReplace;

    public MapBuilder() {
        this.builder = ImmutableMap.builder();
    }

    public MapBuilder put(ResourceLocation key, String val) {
        builder.put(key, new MapValue(val, true));
        return this;
    }

    public MapBuilder putOptional(ResourceLocation key, String val) {
        builder.put(key, new MapValue(val, false));
        return this;
    }

    @Deprecated
    public MapBuilder append(ResourceLocation map) {
        // TODO
        return this;
    }

    @Deprecated
    public MapBuilder appendOptional(ResourceLocation map) {
        // TODO
        return this;
    }

    public MapBuilder setReplace(boolean shouldReplace) {
        this.isReplace = shouldReplace;
        return this;
    }

    public MapFile build() {
        return new MapFile(builder.build(), isReplace);
    }
}
