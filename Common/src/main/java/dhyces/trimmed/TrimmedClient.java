package dhyces.trimmed;

import dhyces.modhelper.network.handler.SimplePacketHandler;
import dhyces.trimmed.impl.client.InfoToast;
import dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dhyces.trimmed.impl.client.override.ItemOverrideReloadListener;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerImplAccessor;
import dhyces.trimmed.impl.network.Networking;
import net.minecraft.Util;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;

import java.util.function.Consumer;

public class TrimmedClient {

    public static final SimplePacketHandler CLIENT_HANDLER = Util.make(new SimplePacketHandler(), handler -> {
        handler.registerPacketConsumer(Networking.SYNC_MAPS, context -> {
            Minecraft.getInstance().player.displayClientMessage(context.message().stack.getDisplayName(), false);
        });
    });

    public static void init() {
        TrimmedSpriteSourceTypes.bootstrap();
        ItemOverrideProviderRegistry.init();
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
        }
    }
}
