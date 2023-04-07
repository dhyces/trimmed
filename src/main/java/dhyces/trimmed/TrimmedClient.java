package dhyces.trimmed;

import dhyces.trimmed.impl.client.override.ItemOverrideReloadListener;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class TrimmedClient {

    static void init(IEventBus modBus) {
        ItemOverrideProviderRegistry.init();
        modBus.addListener(TrimmedClient::registerClientReloadListener);
        modBus.addListener(TrimmedClient::addModels);
    }

    private static void registerClientReloadListener(final RegisterClientReloadListenersEvent event) {
        event.registerReloadListener(new ItemOverrideReloadListener());
    }

    private static void addModels(final ModelEvent.RegisterAdditional event) {
        ItemOverrideReloadListener.getModelsToBake().forEach(event::register);
    }
}
