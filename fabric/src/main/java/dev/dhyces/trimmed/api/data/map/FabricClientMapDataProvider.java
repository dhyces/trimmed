package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.data.PackOutput;

public abstract class FabricClientMapDataProvider<K> extends BaseMapDataProvider<K> {
    public FabricClientMapDataProvider(PackOutput packOutput, String modid) {
        super(packOutput, PackOutput.Target.RESOURCE_PACK, modid, ClientMapManager.PATH + '/');
    }
}
