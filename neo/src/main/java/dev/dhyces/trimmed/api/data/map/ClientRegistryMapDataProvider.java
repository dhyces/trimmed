package dev.dhyces.trimmed.api.data.map;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.data.client.map.appenders.ClientRegistryMapAppender;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class ClientRegistryMapDataProvider<K> extends NeoClientMapDataProvider<K, KeyResolver.RegistryWrapper<K>> {
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;

    public ClientRegistryMapDataProvider(PackOutput packOutput, String modid, ResourceKey<? extends Registry<K>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, TrimmedClientApi.getInstance().getRegistryKeyResolver(registryKey), existingFileHelper);
        this.lookupProviderFuture = lookupProviderFuture;
    }

    public <V> ClientRegistryMapAppender<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(keyResolver.registryKey()));
    }

    protected abstract void addMaps(HolderLookup.Provider lookupProvider);

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return lookupProviderFuture.thenApply(provider -> {
            addMaps(provider);
            complete();
            return provider;
        }).thenCompose(provider -> {
            HolderLookup.RegistryLookup<K> registryLookup = provider.lookupOrThrow(keyResolver.registryKey());

            return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                if (exists(registryLookup, ResourceKey.create(keyResolver.registryKey(), entry.getKey().getMapId()))) {
                    throw new IllegalStateException("Element %s does not exist in %s".formatted(entry.getKey(), keyResolver.registryKey()));
                }
                var codec = MapFile.codec(entry.getKey().getType().getKeyResolver().getCodec(), entry.getKey().getType().getValueCodec());
                Path path = pathProvider.json(entry.getKey().compilePathAndIdNamespace());
                return DataProvider.saveStable(pOutput, provider, codec, Utils.unsafeCast(entry.getValue().build()), path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    protected boolean exists(HolderLookup.RegistryLookup<K> firstLookup, ResourceKey<K> resourceKey) {
        return firstLookup.get(resourceKey).isPresent();
    }

    @Override
    public String getName() {
        return "ClientRegistryMapDataProvider<" + keyResolver.registryKey().location() + "> for " + modid;
    }
}
