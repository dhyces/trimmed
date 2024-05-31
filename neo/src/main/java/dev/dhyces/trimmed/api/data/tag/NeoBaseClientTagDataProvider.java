package dev.dhyces.trimmed.api.data.tag;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.BaseClientTagDataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class NeoBaseClientTagDataProvider<T, R extends KeyResolver<T>> extends BaseClientTagDataProvider<T, R> {
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;

    public NeoBaseClientTagDataProvider(PackOutput packOutput, String modid, ExistingFileHelper.IResourceType resourceType, ExistingFileHelper existingFileHelper, R keyResolver) {
        super(packOutput, modid, keyResolver);
        this.existingFileHelper = existingFileHelper;
        this.resourceType = resourceType;
    }

    @Override
    protected void onBuilderCreation(ResourceLocation id) {
        existingFileHelper.trackGenerated(id, resourceType);
    }

    @Override
    protected boolean doesTagExist(ClientTagKey<T> clientTagKey) {
        return super.doesTagExist(clientTagKey) || existingFileHelper.exists(clientTagKey.getTagId(), resourceType);
    }
}
