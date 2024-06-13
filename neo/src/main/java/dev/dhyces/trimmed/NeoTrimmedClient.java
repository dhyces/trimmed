package dev.dhyces.trimmed;

import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.ClientPlayerNetworkEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.TagsUpdatedEvent;
import net.neoforged.neoforge.event.server.ServerStoppingEvent;

import java.util.Set;

@SuppressWarnings("unused")
@Mod(value = Trimmed.MODID, dist = Dist.CLIENT)
public class NeoTrimmedClient {
    private static Set<ResourceLocation> additionalGeneratedModels;

    public NeoTrimmedClient(IEventBus modBus, ModContainer container) {
        TrimmedClient.init();
        modBus.addListener(this::registerClientReloadListener);
        modBus.addListener(this::addModels);

        NeoForge.EVENT_BUS.addListener(this::tagsSynced);
        NeoForge.EVENT_BUS.addListener(this::onShutdown);
        NeoForge.EVENT_BUS.addListener(this::onLogout);

        TrimmedClient.initApi();
    }

    private void registerClientReloadListener(final RegisterClientReloadListenersEvent event) {
        TrimmedClient.registerClientReloadListener((s, preparableReloadListener) -> event.registerReloadListener(preparableReloadListener));
        TrimmedClient.injectListenersAtBeginning();
    }

    private void addModels(final ModelEvent.RegisterAdditional event) {
        additionalGeneratedModels.forEach(event::register);
    }

    private void tagsSynced(final TagsUpdatedEvent event) {
        TrimmedClient.onTagsSynced(event.getRegistryAccess(), event.shouldUpdateStaticData());
    }

    private void onShutdown(final ServerStoppingEvent event) {
        TrimmedClient.resetSyncedStatus();
    }

    private void onLogout(final ClientPlayerNetworkEvent.LoggingOut event) {
        TrimmedClient.resetSyncedStatus();
    }

    public static void setModels(Set<ResourceLocation> models) {
        additionalGeneratedModels = models;
    }
}
