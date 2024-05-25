package dev.dhyces.trimmed.api.data.maps;

import dev.dhyces.trimmed.impl.client.maps.MapKey;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class BaseMapDataProvider<K> implements DataProvider {

    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;
    protected final Map<MapKey<?, ?>, MapBuilder<?, ?>> builders;
    protected final CompletableFuture<MapLookup> futureLookup;

    public BaseMapDataProvider(PackOutput packOutput, String modid, ExistingFileHelper.IResourceType resourceType, ExistingFileHelper existingFileHelper) {
        this.packOutput = packOutput;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, resourceType.getPrefix());
        this.modid = modid;
        this.existingFileHelper = existingFileHelper;
        this.resourceType = resourceType;
        this.builders = new LinkedHashMap<>();
        futureLookup = new CompletableFuture<>();
    }

    public CompletableFuture<MapLookup> contentsGetter() {
        return futureLookup;
    }

    protected void complete() {
        futureLookup.complete(builders::get);
    }

    protected <V> MapBuilder<K, V> getOrCreateBuilder(MapKey<K, V> mapKey) {
        return (MapBuilder<K, V>) this.builders.computeIfAbsent(mapKey, resourceLocation -> {
            existingFileHelper.trackGenerated(mapKey.getMapId(), resourceType);
            return new MapBuilder<>();
        });
    }

    public interface MapLookup<K, V> extends Function<ResourceLocation, MapBuilder<K, V>> {
        default boolean containsKey(ResourceLocation mapKey) {
            return apply(mapKey) != null;
        }
    }
}
