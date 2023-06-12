package dhyces.trimmed.api.data.maps;

import com.google.common.collect.ImmutableMap;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;

public class MapBuilder {

    private final ImmutableMap.Builder<ResourceLocation, MapValue> builder;
    private final List<MapAppendElement> appendedMaps;
    private boolean isReplace;

    public MapBuilder() {
        this.builder = ImmutableMap.builder();
        this.appendedMaps = new ArrayList<>();
    }

    public MapBuilder put(ResourceLocation key, String val) {
        builder.put(key, new MapValue(val, true));
        return this;
    }

    public MapBuilder putOptional(ResourceLocation key, String val) {
        builder.put(key, new MapValue(val, false));
        return this;
    }

    public MapBuilder append(ResourceLocation map) {
        appendedMaps.add(new MapAppendElement(map, true));
        return this;
    }

    public MapBuilder appendOptional(ResourceLocation map) {
        appendedMaps.add(new MapAppendElement(map, false));
        return this;
    }

    public MapBuilder setReplace(boolean shouldReplace) {
        this.isReplace = shouldReplace;
        return this;
    }

    public MapFile build() {
        return new MapFile(builder.build(), appendedMaps, isReplace);
    }
}
