package dev.dhyces.trimmed.api.data.map;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.data.client.map.appenders.ClientRegistryMapAppender;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class ClientRegistryMapDataProvider<K> extends BaseMapDataProvider<K> {
    private final ResourceKey<? extends Registry<K>> registryKey;
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;

    public ClientRegistryMapDataProvider(PackOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<K>> registryKey, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", ClientMapManager.PATH + Utils.namespacedLocation(registryKey)), existingFileHelper);
        this.lookupProviderFuture = lookupProviderFuture;
        this.registryKey = registryKey;
    }

    public <V> ClientRegistryMapAppender<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(registryKey));
    }

    protected abstract void addMaps(HolderLookup.Provider lookupProvider);

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return lookupProviderFuture.thenApply(provider -> {
            addMaps(provider);
            complete();
            return provider;
        }).thenCompose(provider -> {
            HolderLookup.RegistryLookup<K> registryLookup = provider.lookupOrThrow(registryKey);

            return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                if (exists(registryLookup, ResourceKey.create(registryKey, entry.getKey().getMapId()))) {
                    throw new IllegalStateException("Element %s does not exist in %s".formatted(entry.getKey(), registryKey));
                }
                var codec = MapFile.codec(entry.getKey().getType().getKeyResolver().getCodec(), entry.getKey().getType().getValueCodec());

                DataResult<JsonElement> element = codec.encodeStart(JsonOps.INSTANCE, cast(entry.getValue().build()));
                Path path = pathProvider.json(entry.getKey().getMapId());
                return DataProvider.saveStable(pOutput, element.getOrThrow(), path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    private static <T> T cast(Object o) {
        return (T) o;
    }

    protected boolean exists(HolderLookup.RegistryLookup<K> firstLookup, ResourceKey<K> resourceKey) {
        return firstLookup.get(resourceKey).isPresent();
    }

    @Override
    public String getName() {
        return "ClientRegistryMapDataProvider<" + registryKey.location() + "> for " + modid;
    }
}
