package dev.dhyces.trimmed.api.data.tag;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.BaseClientTagDataProvider;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class NeoBaseClientTagDataProvider<T, R extends KeyResolver<T>> extends BaseClientTagDataProvider<T, R> {
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;

    public NeoBaseClientTagDataProvider(PackOutput packOutput, String modid, R keyResolver, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, keyResolver);
        this.existingFileHelper = existingFileHelper;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", ClientTagManager.PATH + '/' + Utils.namespacedPath(TrimmedClientApi.getInstance().getId(keyResolver)));
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
