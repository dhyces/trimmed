package dev.dhyces.trimmed.api.data.tag;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagEntry;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagFile;
import dev.dhyces.trimmed.api.data.client.tag.appenders.ClientRegistryTagAppender;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.Unit;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ClientRegistryTagDataProvider<T> extends NeoBaseClientTagDataProvider<T, KeyResolver.RegistryResolver<T>> {
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;
    private final CompletableFuture<Unit> completed;

    public ClientRegistryTagDataProvider(PackOutput packOutput, String modid, ResourceKey<? extends Registry<T>> registryKey, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, TrimmedClientApi.getInstance().getRegistryKeyResolver(registryKey), existingFileHelper);
        this.lookupProviderFuture = lookupProviderFuture;
        this.completed = new CompletableFuture<>();
    }

    protected abstract void addTags(HolderLookup.Provider lookupProvider);

    public ClientRegistryTagAppender<T> tag(ClientTagKey<T> clientTagKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryTagAppender<>(getOrCreateBuilder(clientTagKey), lookupProvider.lookupOrThrow(keyResolver.getKey()));
    }

    public ClientRegistryTagAppender.Mapped<T> tag(ClientTagKey<T> clientTagKey, HolderLookup.Provider lookupProvider, Function<T, @Nullable ResourceLocation> encoder) {
        return new ClientRegistryTagAppender.Mapped<>(getOrCreateBuilder(clientTagKey), lookupProvider.lookupOrThrow(keyResolver.getKey()), encoder);
    }

    protected CompletableFuture<HolderLookup.Provider> createContentProvider() {
        return lookupProviderFuture.thenApply(provider -> {
            addTags(provider);
            return provider;
        });
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        return createContentProvider().thenApply(provider -> {
            this.completed.complete(Unit.INSTANCE);
            return provider;
        }).thenCompose(provider -> {
            Predicate<ResourceLocation> registryCheck = id -> provider.lookup(keyResolver.getKey()).orElseThrow(() ->
                    new IllegalStateException("Vanilla registry " + keyResolver.getKey().location() + " is not present.")
            ).get(ResourceKey.create(keyResolver.getKey(), id)).isPresent();

            return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                ClientTagFile tagFile = entry.getValue().build();
                List<ClientTagEntry> errors = tagFile.entries().stream().filter(tagEntry ->
                        !tagEntry.verifyExists(registryCheck, this::doesTagExist)
                ).toList();
                if (!errors.isEmpty()) {
                    throw new IllegalStateException("Tag entries [%s] were not found for registry %s".formatted(errors.stream().map(Object::toString).collect(Collectors.joining(",")), keyResolver.getKey()));
                } else {
                    Path filePath = pathProvider.json(entry.getKey());
                    return DataProvider.saveStable(pOutput, provider, ClientTagFile.CODEC, tagFile, filePath);
                }
            }).toArray(CompletableFuture[]::new));
        });
    }

    @Override
    public String getName() {
        return "ClientRegistryTagProvider<" + keyResolver.getKey().location() + "> for " + modid;
    }
}
