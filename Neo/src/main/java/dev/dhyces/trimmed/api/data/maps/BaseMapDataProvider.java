package dev.dhyces.trimmed.api.data.maps;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class BaseMapDataProvider implements DataProvider {

    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;
    protected final Map<ResourceLocation, MapBuilder> builders;
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

    protected MapBuilder getOrCreateBuilder(ResourceLocation clientMapLocation) {
        return this.builders.computeIfAbsent(clientMapLocation, resourceLocation -> {
            existingFileHelper.trackGenerated(resourceLocation, resourceType);
            return new MapBuilder();
        });
    }

    public interface MapLookup extends Function<ResourceLocation, MapBuilder> {
        default boolean containsKey(ResourceLocation mapKey) {
            return apply(mapKey) != null;
        }
    }
}
