package dev.dhyces.trimmed.api.data.maps;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.Trimmed;
import dev.dhyces.trimmed.api.data.maps.appenders.ClientRegistryMapAppender;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientRegistryMapDataProvider<T> extends BaseMapDataProvider {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;
    private final CompletableFuture<Unit> delayedContent;

    public ClientRegistryMapDataProvider(FabricDataOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<T>> registryKey) {
        super(packOutput, modid, "maps/" + Utils.prefix(registryKey));
        this.lookupProviderFuture = lookupProviderFuture;
        this.registryKey = registryKey;
        this.delayedContent = new CompletableFuture<>();
    }

    public ClientRegistryMapAppender<T, String> map(ClientRegistryMapKey<T> clientRegistryMapKey) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), Function.identity(), registryKey);
    }

    public <V> ClientRegistryMapAppender<T, V> mapWithMapper(ClientRegistryMapKey<T> clientRegistryMapKey, Function<V, String> mapper) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), mapper, registryKey);
    }

    public ClientRegistryMapAppender.RegistryAware<T, String> registryAware(ClientRegistryMapKey<T> clientRegistryMapKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender.RegistryAware<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), Function.identity(), registryKey, lookupProvider);
    }

    public <V> ClientRegistryMapAppender.RegistryAware<T, V> registryAwareWithMapper(ClientRegistryMapKey<T> clientRegistryMapKey, Function<V, String> mapper, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender.RegistryAware<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), mapper, registryKey, lookupProvider);
    }

    protected abstract void addMaps(HolderLookup.Provider lookupProvider);

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return lookupProviderFuture.thenApply(provider -> {
            addMaps(provider);
            delayedContent.complete(Unit.INSTANCE);
            return provider;
        }).thenCompose(provider -> {
            Optional<HolderLookup.RegistryLookup<T>> registryLookup = provider.lookup(registryKey);

            return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                if (exists(registryLookup, ResourceKey.create(registryKey, entry.getKey()))) {
                    throw new IllegalStateException("Element %s does not exist in %s".formatted(entry.getKey(), registryKey));
                }

                DataResult<JsonElement> element = MapFile.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue().build());
                Path path = pathProvider.json(entry.getKey());
                return DataProvider.saveStable(pOutput, element.getOrThrow(false, Trimmed.LOGGER::error), path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    protected boolean exists(Optional<HolderLookup.RegistryLookup<T>> firstLookup, ResourceKey<T> resourceKey) {
        return firstLookup.flatMap(tRegistryLookup -> tRegistryLookup.get(resourceKey)).isPresent();
    }

    @Override
    public String getName() {
        return "ClientRegistryMapDataProvider<" + registryKey.location() + "> for " + modid;
    }
}
