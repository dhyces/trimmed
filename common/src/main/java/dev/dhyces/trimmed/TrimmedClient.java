package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import dev.dhyces.trimmed.impl.client.TrimmedClientRegistrationImpl;
import dev.dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.impl.client.models.override.ItemOverrideReloadListener;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceLoader;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceRegistry;
import dev.dhyces.trimmed.impl.client.models.source.NamedModel;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dev.dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerImplAccessor;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dev.dhyces.trimmed.modhelper.services.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class TrimmedClient {

    public static void init() {
        KeyResolvers.register(Trimmed.id("texture"), ClientKeyResolvers.TEXTURE);
        ClientMapTypes.init();
        ClientMapManager.registerBaseKey(ClientMapKeys.MATERIAL_SUFFIXES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_MATERIAL_OVERRIDES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_OVERLAYS);
        ModelSourceRegistry.init();
        TrimmedSpriteSourceTypes.bootstrap();
        ItemOverrideProviderRegistry.init();
    }

    public static void initApi() {
        TrimmedClientApiEntrypoint.TrimmedClientRegistration registration = new TrimmedClientRegistrationImpl();
        for (ModApiConsumer<TrimmedClientApiEntrypoint> entrypoint : Services.CLIENT_HELPER.getClientApiConsumers()) {
            entrypoint.entrypoint().registration(registration);
        }
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
//            ClientTagManager.updateDatapacksSynced(registryAccess);
        }
    }

    public static CompletableFuture<Collection<NamedModel>> startGeneratingModels(ResourceManager resourceManager, Executor executor) {
        return ModelTemplateManager.load(resourceManager, executor)
                .thenComposeAsync(templateManager -> ModelSourceLoader.load(templateManager, resourceManager, executor));
    }
}
