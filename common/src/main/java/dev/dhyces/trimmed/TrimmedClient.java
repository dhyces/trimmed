package dev.dhyces.trimmed;

import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.client.TrimmedClientApiEntrypoint;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.impl.ModApiConsumer;
import dev.dhyces.trimmed.impl.client.GameRegistryHolder;
import dev.dhyces.trimmed.impl.client.TrimmedClientRegistrationImpl;
import dev.dhyces.trimmed.impl.client.atlas.OpenPalettedPermutations;
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
import net.minecraft.client.renderer.texture.atlas.SpriteSource;
import net.minecraft.client.renderer.texture.atlas.SpriteSourceType;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;

public class TrimmedClient {
    private static GameRegistryHolder staticAccess;
    public static GameRegistryHolder getStaticHolder() {
        if (staticAccess == null) {
            staticAccess = new GameRegistryHolder(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY), false);
        }
        return staticAccess;
    }

    public static void init() {
        KeyResolvers.register(Trimmed.id("texture"), ClientKeyResolvers.TEXTURE);
        ClientMapTypes.init();
        ClientMapManager.registerBaseKey(ClientMapKeys.MATERIAL_SUFFIXES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_MATERIAL_OVERRIDES);
        ClientMapManager.registerBaseKey(ClientMapKeys.TRIM_OVERLAYS);
        ModelSourceRegistry.init();
        ItemOverrideProviderRegistry.init();
    }

    public static void initApi() {
        TrimmedClientApiEntrypoint.TrimmedClientRegistration registration = new TrimmedClientRegistrationImpl();
        for (ModApiConsumer<TrimmedClientApiEntrypoint> consumer : Services.CLIENT_HELPER.getClientApiConsumers()) {
            consumer.entrypoint().registration(registration);
        }
    }

    public static void registerSpriteSourceTypes(BiFunction<ResourceLocation, MapCodec<? extends SpriteSource>, SpriteSourceType> registrar) {
        TrimmedSpriteSourceTypes.bootstrap(registrar);
    }

    public static void registerClientReloadListener(BiConsumer<String, PreparableReloadListener> eventConsumer) {
        eventConsumer.accept("item_model_overrides", new ItemOverrideReloadListener());
    }

    public static void injectListenersAtBeginning() {
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientMapManager());
        ((ReloadableResourceManagerImplAccessor)Minecraft.getInstance().getResourceManager()).getListeners().add(0, new ClientTagManager());
    }

    public static void onTagsSynced(RegistryAccess registryAccess, boolean shouldUpdateStatic) {
        staticAccess = new GameRegistryHolder(registryAccess, true);
        if (shouldUpdateStatic) { //TODO: Disabled the toast for now. Use toast later when a datapack registry queued
//            if (Minecraft.getInstance().player != null) {
//                Minecraft.getInstance().getToasts().addToast(InfoToast.reloadClientInfo());
//            }
            ClientTagManager.updateDatapacksSynced(staticAccess);
            ClientMapManager.updateDatapacksSynced(staticAccess);
        }
    }

    public static void resetSyncedStatus() {
        staticAccess = null;
    }

    public static CompletableFuture<Collection<NamedModel>> startGeneratingModels(ResourceManager resourceManager, Executor executor) {
        return ModelTemplateManager.load(resourceManager, executor)
                .thenComposeAsync(templateManager -> ModelSourceLoader.load(templateManager, resourceManager, executor));
    }
}
