package dhyces.trimmed;

import dhyces.modhelper.network.Designation;
import dhyces.modhelper.network.SimpleChannelHandler;
import dhyces.modhelper.network.SimpleNetworkHandler;
import dhyces.modhelper.services.Services;
import dhyces.trimmed.impl.client.InfoToast;
import dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dhyces.trimmed.impl.client.override.ItemOverrideReloadListener;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerImplAccessor;
import dhyces.trimmed.impl.network.ClientNetworkHandler;
import dhyces.trimmed.impl.network.Packets;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public class TrimmedClient {

    public static void init() {
        TrimmedSpriteSourceTypes.bootstrap();
        ItemOverrideProviderRegistry.init();

        Trimmed.CHANNEL_HANDLER.registerHandler(ClientNetworkHandler::new);
    }

    public static void registerClientReloadListener(Consumer<PreparableReloadListener> eventConsumer) {
        eventConsumer.accept(new ItemOverrideReloadListener());
    }

    public static void injectListenersAtBeginning() {
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    public static void addModels(Consumer<ModelResourceLocation> eventConsumer) {
        ItemOverrideReloadListener.getModelsToBake().forEach(eventConsumer);
    }

    public static void onTagsSynced(RegistryAccess registryAccess, boolean shouldUpdateStatic) {
        if (shouldUpdateStatic) {
            if (Minecraft.getInstance().player != null) {
                Minecraft.getInstance().getToasts().addToast(InfoToast.reloadClientInfo());
            }
            ClientTagManager.updateDatapacksSynced(registryAccess);
            ClientMapManager.updateDatapacksSynced(registryAccess);
        }
    }
}
