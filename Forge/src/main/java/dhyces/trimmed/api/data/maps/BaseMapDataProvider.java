package dhyces.trimmed.api.data.maps;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseMapDataProvider implements DataProvider {

    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;
    protected final Map<ResourceLocation, MapBuilder> builders;

    public BaseMapDataProvider(PackOutput packOutput, String modid, ExistingFileHelper.IResourceType resourceType, ExistingFileHelper existingFileHelper) {
        this.packOutput = packOutput;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, resourceType.getPrefix());
        this.modid = modid;
        this.existingFileHelper = existingFileHelper;
        this.resourceType = resourceType;
        this.builders = new LinkedHashMap<>();
    }

    protected MapBuilder getOrCreateBuilder(ResourceLocation clientMapLocation) {
        return this.builders.computeIfAbsent(clientMapLocation, resourceLocation -> {
            existingFileHelper.trackGenerated(resourceLocation, resourceType);
            return new MapBuilder();
        });
    }
}
