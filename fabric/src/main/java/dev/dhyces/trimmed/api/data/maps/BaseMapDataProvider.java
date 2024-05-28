package dev.dhyces.trimmed.api.data.maps;

import dev.dhyces.trimmed.api.maps.MapKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseMapDataProvider implements DataProvider {

    protected final FabricDataOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final Map<MapKey<?, ?>, MapBuilder<?, ?>> builders;

    public BaseMapDataProvider(FabricDataOutput packOutput, String modid, String prefix) {
        this.packOutput = packOutput;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, prefix);
        this.modid = modid;
        this.builders = new LinkedHashMap<>();
    }

    protected <K, V> MapBuilder<K, V> getOrCreateBuilder(MapKey<K, V> mapKey) {
        return (MapBuilder<K, V>) this.builders.computeIfAbsent(mapKey, resourceLocation -> new MapBuilder<>());
    }
}
