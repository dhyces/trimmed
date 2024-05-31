package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

public abstract class NeoClientMapDataProvider<K, R extends KeyResolver<K>> extends NeoBaseMapDataProvider<K, R> {
    public NeoClientMapDataProvider(PackOutput packOutput, String modid, R keyResolver, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, keyResolver, new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", ClientMapManager.PATH + '/' + Utils.namespacedPath(TrimmedClientApi.getInstance().getId(keyResolver))), existingFileHelper);
    }
}
