package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.data.map.appenders.RegistryMapAppender;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.util.Utils;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientRegistryMapDataProvider<K> extends NeoClientMapDataProvider<K> {
    protected final ResourceKey<? extends Registry<K>> registryKey;
    protected final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;

    public ClientRegistryMapDataProvider(PackOutput packOutput, String modid, ResourceKey<? extends Registry<K>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, existingFileHelper);
        this.registryKey = registryKey;
        this.lookupProviderFuture = lookupProviderFuture;
    }

    public <V> RegistryMapAppender<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider) {
        return new RegistryMapAppender<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(registryKey));
    }

    public <V> RegistryMapAppender.Mapped<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider, Function<K, @Nullable ResourceLocation> encoder) {
        return new RegistryMapAppender.Mapped<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(registryKey), encoder);
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
                var codec = MapFile.codec(entry.getKey().getType().getValueCodec());
                Path path = pathProvider.json(entry.getKey().compilePathAndIdNamespace().withPrefix(Utils.namespacedLocation(registryKey) + '/'));
                return DataProvider.saveStable(pOutput, provider, codec, Utils.unsafeCast(entry.getValue().build()), path);
            }).toArray(CompletableFuture[]::new));
        });
    }

    protected boolean exists(HolderLookup.RegistryLookup<K> firstLookup, ResourceKey<K> resourceKey) {
        return firstLookup.get(resourceKey).isPresent();
    }

    @Override
    public String getName() {
        return "ClientRegistryMapDataProvider<" + registryKey.location() + "> for " + modid;
    }
}
