package dev.dhyces.trimmed.api.data.tag;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.data.client.tag.BaseClientTagDataProvider;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagEntry;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagFile;
import dev.dhyces.trimmed.api.data.client.tag.appenders.ClientRegistryTagAppender;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.util.Unit;

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public abstract class ClientRegistryTagDataProvider<T> extends BaseClientTagDataProvider<T, KeyResolver.RegistryWrapper<T>> {
    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;
    private final CompletableFuture<Unit> completed;

    public ClientRegistryTagDataProvider(PackOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<T>> registryKey) {
        super(packOutput, modid, TrimmedClientApi.getInstance().getRegistryKeyResolver(registryKey));
        this.lookupProviderFuture = lookupProviderFuture;
        this.completed = new CompletableFuture<>();
    }

    protected abstract void addTags(HolderLookup.Provider lookupProvider);

    public ClientRegistryTagAppender<T> tag(ClientTagKey<T> clientTagKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryTagAppender<>(getOrCreateBuilder(clientTagKey), lookupProvider.lookupOrThrow(keyResolver.registryKey()));
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
                    Set<T> registrySet = provider.lookup(keyResolver.registryKey()).orElseThrow(() ->
                        new IllegalStateException("Vanilla registry " + keyResolver.registryKey().location() + " is not present.")
                    ).listElements().map(Holder.Reference::value).collect(Collectors.toUnmodifiableSet());

                    return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                        ClientTagFile<T> tagFile = entry.getValue().build();
                        List<ClientTagEntry<T>> errors = tagFile.entries().stream().filter(tagEntry ->
                            !tagEntry.verifyExists(registrySet::contains, this::doesTagExist)
                        ).toList();
                        if (!errors.isEmpty()) {
                            throw new IllegalStateException("Tag entries [%s] were not found for registry %s".formatted(errors.stream().map(Object::toString).collect(Collectors.joining(",")), keyResolver.registryKey()));
                        } else {
                            Path filePath = pathProvider.json(entry.getKey());
                            return DataProvider.saveStable(pOutput, provider, ClientTagFile.codec(keyResolver), tagFile, filePath);
                        }
                    }).toArray(CompletableFuture[]::new));
                });
    }

    @Override
    public String getName() {
        return "ClientRegistryTagProvider<" + keyResolver.registryKey().location() + "> for " + modid;
    }
}
