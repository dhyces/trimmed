package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class NeoBaseMapDataProvider<K> extends BaseMapDataProvider<K> {
    protected final ExistingFileHelper existingFileHelper;
    protected final ExistingFileHelper.IResourceType resourceType;

    public NeoBaseMapDataProvider(PackOutput packOutput, String modid, ExistingFileHelper.IResourceType resourceType, ExistingFileHelper existingFileHelper) {
        super(packOutput, convertToTarget(resourceType.getPackType()), modid, resourceType.getPrefix());
        this.existingFileHelper = existingFileHelper;
        this.resourceType = resourceType;
    }

    public static PackOutput.Target convertToTarget(PackType packType) {
        return switch (packType) {
            case SERVER_DATA -> PackOutput.Target.DATA_PACK;
            case CLIENT_RESOURCES -> PackOutput.Target.RESOURCE_PACK;
        };
    }

    @Override
    protected <V> void onBuilderCreation(MapKey<K, V> mapKey) {
        existingFileHelper.trackGenerated(mapKey.getMapId(), resourceType);
    }
}
