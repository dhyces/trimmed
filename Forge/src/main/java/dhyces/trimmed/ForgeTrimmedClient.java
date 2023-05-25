package dhyces.trimmed;

import net.minecraftforge.client.event.ModelEvent;
import net.minecraftforge.client.event.RegisterClientReloadListenersEvent;
import net.minecraftforge.event.TagsUpdatedEvent;
import net.minecraftforge.eventbus.api.IEventBus;

public class ForgeTrimmedClient {

    static void init(IEventBus forgeBus, IEventBus modBus) {
        TrimmedClient.init();
        modBus.addListener(ForgeTrimmedClient::registerClientReloadListener);
        modBus.addListener(ForgeTrimmedClient::addModels);

        forgeBus.addListener(ForgeTrimmedClient::tagsSynced);
    }

    private static void registerClientReloadListener(final RegisterClientReloadListenersEvent event) {
        TrimmedClient.registerClientReloadListener(event::registerReloadListener);
        TrimmedClient.injectListenersAtBeginning();
    }

    private static void addModels(final ModelEvent.RegisterAdditional event) {
        TrimmedClient.addModels(event::register);
    }

    private static void tagsSynced(final TagsUpdatedEvent event) {
        TrimmedClient.onTagsSynced(event.getRegistryAccess(), event.shouldUpdateStaticData());
    }
}
