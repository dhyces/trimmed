package dev.dhyces.trimmed.api.data.map;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.data.client.map.appenders.ClientRegistryMapAppender;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.resources.ResourceKey;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public abstract class ClientRegistryMapDataProvider<K> extends FabricClientMapDataProvider<K, KeyResolver.RegistryWrapper<K>> {
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;
    private final CompletableFuture<Unit> delayedContent;

    public ClientRegistryMapDataProvider(FabricDataOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<K>> registryKey) {
        super(packOutput, modid, TrimmedClientApi.getInstance().getRegistryKeyResolver(registryKey));
        this.lookupProviderFuture = lookupProviderFuture;
        this.delayedContent = new CompletableFuture<>();
    }

    public <V> ClientRegistryMapAppender<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(keyResolver.registryKey()));
    }

    protected abstract void addMaps(HolderLookup.Provider lookupProvider);

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return lookupProviderFuture.thenApply(provider -> {
            addMaps(provider);
            delayedContent.complete(Unit.INSTANCE);
            return provider;
        }).thenCompose(provider -> {
            Optional<HolderLookup.RegistryLookup<K>> registryLookup = provider.lookup(keyResolver.registryKey());

            return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                if (exists(registryLookup, ResourceKey.create(keyResolver.registryKey(), entry.getKey().getMapId()))) {
                    throw new IllegalStateException("Element %s does not exist in %s".formatted(entry.getKey(), keyResolver.registryKey()));
                }

                var codec = MapFile.codec(entry.getKey().getType().getKeyResolver().getCodec(), entry.getKey().getType().getValueCodec());
                Path path = pathProvider.json(entry.getKey().getMapId());
                return DataProvider.saveStable(pOutput, provider, codec, Utils.unsafeCast(entry.getValue().build()), path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    protected boolean exists(Optional<HolderLookup.RegistryLookup<K>> firstLookup, ResourceKey<K> resourceKey) {
        return firstLookup.flatMap(tRegistryLookup -> tRegistryLookup.get(resourceKey)).isPresent();
    }

    @Override
    public String getName() {
        return "ClientRegistryMapDataProvider<" + keyResolver.registryKey().location() + "> for " + modid;
    }
}
