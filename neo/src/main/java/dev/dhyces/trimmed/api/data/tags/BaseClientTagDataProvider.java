package dev.dhyces.trimmed.api.data.tags;

import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.LinkedHashMap;
import java.util.Map;

public abstract class BaseClientTagDataProvider implements DataProvider {

    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;
    protected final Map<ResourceLocation, TagBuilder> builders = new LinkedHashMap<>();

    public BaseClientTagDataProvider(PackOutput packOutput, String modid, ExistingFileHelper.IResourceType resourceType, ExistingFileHelper existingFileHelper) {
        this.packOutput = packOutput;
        this.modid = modid;
        this.existingFileHelper = existingFileHelper;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, resourceType.getPrefix());
        this.resourceType = resourceType;
    }

    protected TagBuilder getOrCreateBuilder(ResourceLocation clientTagLocation) {
        return this.builders.computeIfAbsent(clientTagLocation, resourceLocation -> {
            existingFileHelper.trackGenerated(resourceLocation, resourceType);
            return TagBuilder.create();
        });
    }
}
