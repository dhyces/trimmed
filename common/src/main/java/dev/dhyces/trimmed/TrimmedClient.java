package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.client.map.ClientMapKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dev.dhyces.trimmed.impl.client.maps.MapKeyResolvers;
import dev.dhyces.trimmed.impl.client.models.override.ItemOverrideReloadListener;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceRegistry;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dev.dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerImplAccessor;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;

public class TrimmedClient {

    public static void init() {
        MapKeyResolvers.register(Trimmed.id("texture"), ClientMapKeyResolvers.TEXTURE);
        ClientMapTypes.init();
        ClientMapManager.registerBaseKey(ClientMapKeys.MATERIAL_SUFFIXES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_MATERIAL_OVERRIDES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_OVERLAYS);
        ModelSourceRegistry.init();
        TrimmedSpriteSourceTypes.bootstrap();
        ItemOverrideProviderRegistry.init();
    }

    public static void registerClientReloadListener(BiConsumer<String, PreparableReloadListener> eventConsumer) {
        eventConsumer.accept("item_model_overrides", new ItemOverrideReloadListener());
    }

    public static void injectListenersAtBeginning() {
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    public static void onTagsSynced(RegistryAccess registryAccess, boolean shouldUpdateStatic) {
        if (shouldUpdateStatic) { //TODO: Disabled the toast for now. Use toast later when a datapack registry queued
//            if (Minecraft.getInstance().player != null) {
//                Minecraft.getInstance().getToasts().addToast(InfoToast.reloadClientInfo());
//            }
            ClientTagManager.updateDatapacksSynced(registryAccess);
        }
    }
}
