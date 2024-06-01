package dev.dhyces.trimmed.api.data.model.source;

import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class ModelSourceDataProvider extends BaseModelSourceDataProvider {
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;

    public ModelSourceDataProvider(PackOutput packOutput, String modid, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid);
        this.existingFileHelper = existingFileHelper;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "trimmed/model_generators");
    }

    @Override
    protected void onAdd(ResourceLocation id) {
        existingFileHelper.trackGenerated(id, resourceType);
    }
}
