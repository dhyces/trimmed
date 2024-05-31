package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.data.PackOutput;

public abstract class FabricClientMapDataProvider<K, R extends KeyResolver<K>> extends BaseMapDataProvider<K, R> {
    public FabricClientMapDataProvider(PackOutput packOutput, String modid, R keyResolver) {
        super(packOutput, PackOutput.Target.RESOURCE_PACK, modid, ClientMapManager.PATH + '/' + Utils.namespacedPath(TrimmedClientApi.getInstance().getId(keyResolver)), keyResolver);
    }
}
