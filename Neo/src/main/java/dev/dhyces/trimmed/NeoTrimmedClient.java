package dev.dhyces.trimmed;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.event.TagsUpdatedEvent;

public class NeoTrimmedClient {
    static void init(IEventBus neoBus, IEventBus modBus) {
        TrimmedClient.init();
        modBus.addListener(NeoTrimmedClient::registerClientReloadListener);
        modBus.addListener(NeoTrimmedClient::addModels);

        neoBus.addListener(NeoTrimmedClient::tagsSynced);
    }

    private static void registerClientReloadListener(final RegisterClientReloadListenersEvent event) {
        TrimmedClient.registerClientReloadListener((s, preparableReloadListener) -> event.registerReloadListener(preparableReloadListener));
        TrimmedClient.injectListenersAtBeginning();
    }

    private static void addModels(final ModelEvent.RegisterAdditional event) {
        TrimmedClient.addModels(event::register);
    }

    private static void tagsSynced(final TagsUpdatedEvent event) {
        TrimmedClient.onTagsSynced(event.getRegistryAccess(), event.shouldUpdateStaticData());
    }
}
