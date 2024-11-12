package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class NeoClientMapDataProvider<K> extends NeoBaseMapDataProvider<K> {
    public NeoClientMapDataProvider(PackOutput packOutput, String modid, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", ClientMapManager.PATH + '/'), existingFileHelper);
    }
}
