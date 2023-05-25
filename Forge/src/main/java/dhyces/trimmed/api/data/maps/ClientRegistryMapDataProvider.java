package dhyces.trimmed.api.data.maps;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Unit;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.appenders.ClientRegistryMapAppender;
import dhyces.trimmed.api.util.Utils;
import dhyces.trimmed.impl.client.maps.ClientRegistryMapKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegistryManager;

import java.nio.file.Path;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientRegistryMapDataProvider<T> extends BaseMapDataProvider {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;
    private final CompletableFuture<Unit> completed;

    public ClientRegistryMapDataProvider(PackOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<T>> registryKey, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "maps/" + Utils.prefix(registryKey)), existingFileHelper);
        this.lookupProviderFuture = lookupProviderFuture;
        this.registryKey = registryKey;
        this.completed = new CompletableFuture<>();
    }

    public ClientRegistryMapAppender<T, String> map(ClientRegistryMapKey<T> clientRegistryMapKey) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), Function.identity(), registryKey);
    }

    public <V> ClientRegistryMapAppender<T, V> mapWithMapper(ClientRegistryMapKey<T> clientRegistryMapKey, Class<V> valueClass, Function<V, String> mapper) {
        return new ClientRegistryMapAppender<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), mapper, registryKey);
    }

    public ClientRegistryMapAppender.RegistryAware<T, String> registryAware(ClientRegistryMapKey<T> clientRegistryMapKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender.RegistryAware<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), Function.identity(), registryKey, lookupProvider);
    }

    public <V> ClientRegistryMapAppender.RegistryAware<T, V> registryAwareWithMapper(ClientRegistryMapKey<T> clientRegistryMapKey, Class<V> valueClass, Function<V, String> mapper, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryMapAppender.RegistryAware<>(getOrCreateBuilder(clientRegistryMapKey.getMapId()), mapper, registryKey, lookupProvider);
    }

    protected abstract void addMaps(HolderLookup.Provider lookupProvider);

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return lookupProviderFuture.thenApply(provider -> {
            addMaps(provider);
            completed.complete(Unit.INSTANCE);
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
        return firstLookup.flatMap(tRegistryLookup -> tRegistryLookup.get(resourceKey)).isPresent() || checkForgeRegistries(resourceKey);
    }

    protected boolean checkForgeRegistries(ResourceKey<T> resourceKey) {
        IForgeRegistry<T> registry = RegistryManager.ACTIVE.getRegistry(registryKey);
        return registry != null && registry.containsKey(resourceKey.location());
    }

    @Override
    public String getName() {
        return "ClientRegistryMapDataProvider<" + registryKey.location() + "> for " + modid;
    }
}
