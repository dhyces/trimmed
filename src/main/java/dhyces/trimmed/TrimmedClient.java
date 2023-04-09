package dhyces.trimmed;

import dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dhyces.trimmed.impl.client.maps.ClientMapManager;
import dhyces.trimmed.impl.client.override.ItemOverrideReloadListener;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import dhyces.trimmed.impl.client.tags.ClientTagManager;
import dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerAccessor;
import net.minecraft.client.Minecraft;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedClient {

    static void init(IEventBus modBus) {
        ItemOverrideProviderRegistry.init();
        TrimmedSpriteSourceTypes.bootstrap();
        modBus.addListener(TrimmedClient::registerClientReloadListener);
        modBus.addListener(TrimmedClient::addModels);
    }

    private static void registerClientReloadListener(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ItemOverrideReloadListener());
        ((ReloadableResourceManagerAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    private static void addModels(final ModelEvent.RegisterAdditional event) {
        ItemOverrideReloadListener.getModelsToBake().forEach(event::register);
    }
}
