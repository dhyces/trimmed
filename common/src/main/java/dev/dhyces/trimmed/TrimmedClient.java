package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import dev.dhyces.trimmed.impl.client.GameRegistryHolder;
import dev.dhyces.trimmed.impl.client.TrimmedClientRegistrationImpl;
import dev.dhyces.trimmed.impl.client.atlas.TrimmedSpriteSourceTypes;
import dev.dhyces.trimmed.api.maps.KeyResolvers;
import dev.dhyces.trimmed.impl.client.models.override.ItemOverrideReloadListener;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceLoader;
import dev.dhyces.trimmed.impl.client.models.source.ModelSourceRegistry;
import dev.dhyces.trimmed.impl.client.models.source.NamedModel;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import dev.dhyces.trimmed.impl.mixin.client.ReloadableResourceManagerAccessor;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import dev.dhyces.trimmed.modhelper.services.Services;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;

public class TrimmedClient {
    private static GameRegistryHolder staticAccess;
    public static GameRegistryHolder getStaticHolder() {
        if (staticAccess == null) {
            staticAccess = new GameRegistryHolder(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), false);
        }
        return staticAccess;
    }

    static Set<ResourceLocation> additionalGeneratedModels;

    static void init() {
        KeyResolvers.register(TrimmedReference.id("texture"), ClientKeyResolvers.TEXTURE);
        ClientMapTypes.init();
        ClientMapManager.registerBaseKey(ClientMapKeys.MATERIAL_SUFFIXES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_MATERIAL_OVERRIDES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_OVERLAYS);
        ModelSourceRegistry.init();
        ItemOverrideProviderRegistry.init();
    }

    static void initApi() {
        TrimmedClientApiEntrypoint.TrimmedClientRegistration registration = new TrimmedClientRegistrationImpl();
        for (ModApiConsumer<TrimmedClientApiEntrypoint> consumer : Services.CLIENT_HELPER.getClientApiConsumers()) {
            consumer.entrypoint().registration(registration);
        }
    }

    static void registerSpriteSourceTypes(BiConsumer<ResourceLocation, SpriteSourceType> registrar) {
        TrimmedSpriteSourceTypes.bootstrap(registrar);
    }

    static void registerClientReloadListener(BiConsumer<String, PreparableReloadListener> eventConsumer) {
        eventConsumer.accept("item_model_overrides", new ItemOverrideReloadListener());
    }

    public static void injectListenersAtBeginning() {
        ((ReloadableResourceManagerAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    static void onTagsSynced(HolderLookup.Provider lookupProvider, boolean shouldUpdateStatic) {
        staticAccess = new GameRegistryHolder(lookupProvider, true);
        if (shouldUpdateStatic) { //TODO: Disabled the toast for now. Use toast later when a datapack registry queued
//            if (Minecraft.getInstance().player != null) {
//                Minecraft.getInstance().getToasts().addToast(InfoToast.reloadClientInfo());
//            }
            ClientTagManager.updateDatapacksSynced(staticAccess);
            ClientMapManager.updateDatapacksSynced(staticAccess);
        }
    }

    static void resetSyncedStatus() {
        staticAccess = null;
    }

    public static CompletableFuture<Collection<NamedModel>> startGeneratingModels(ResourceManager resourceManager, Executor executor) {
        return ModelTemplateManager.load(resourceManager, executor)
                .thenComposeAsync(templateManager -> ModelSourceLoader.load(templateManager, resourceManager, executor));
    }

    public static void setModels(Set<ResourceLocation> models) {
        additionalGeneratedModels = models;
    }
}
