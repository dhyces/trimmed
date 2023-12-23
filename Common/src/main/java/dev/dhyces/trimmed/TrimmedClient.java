package dev.dhyces.trimmed;

import dev.dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dev.dhyces.trimmed.impl.client.models.override.ItemOverrideReloadListener;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dev.dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerImplAccessor;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
//import dhyces.trimmed.impl.network.Networking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TrimmedClient {

//    public static final SimplePacketHandler CLIENT_HANDLER = Util.make(new SimplePacketHandler(), handler -> {
//        handler.registerPacketConsumer(Networking.SYNC_MAPS, context -> {
//            Minecraft.getInstance().player.displayClientMessage(context.message().stack.getDisplayName(), false);
//        });
//    });

    public static void init() {
        TrimmedSpriteSourceTypes.bootstrap();
        ModelTemplateManager.init();
        ItemOverrideProviderRegistry.init();
    }

    public static void registerClientReloadListener(BiConsumer<String, PreparableReloadListener> eventConsumer) {
        eventConsumer.accept("item_model_overrides", new ItemOverrideReloadListener());
        eventConsumer.accept("model_templates", ModelTemplateManager.getInstance());
    }

    public static void injectListenersAtBeginning() {
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    public static void addModels(Consumer<ModelResourceLocation> eventConsumer) {
        ItemOverrideReloadListener.getModelsToBake().forEach(eventConsumer);
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
