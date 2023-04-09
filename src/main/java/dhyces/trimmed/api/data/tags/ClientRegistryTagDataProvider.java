package dhyces.trimmed.api.data.tags;

import com.google.gson.JsonElement;
import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.tags.appenders.ClientRegistryTagAppender;
import dhyces.trimmed.api.data.tags.appenders.ClientTagAppender;
import dhyces.trimmed.impl.client.tags.ClientRegistryTagKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagBuilder;
import net.minecraft.tags.TagEntry;
import net.minecraft.tags.TagFile;
import net.minecraft.util.Unit;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.registries.RegistryManager;

import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public abstract class ClientRegistryTagDataProvider<T> extends BaseClientTagDataProvider {

    private final CompletableFuture<HolderLookup.Provider> lookupProviderFuture;
    private final CompletableFuture<Unit> completed;
    protected final ResourceKey<? extends Registry<T>> registryResourceKey;


    public ClientRegistryTagDataProvider(PackOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<T>> registryResourceKey, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "tags/" + registryResourceKey.location().getPath()), existingFileHelper);
        this.lookupProviderFuture = lookupProviderFuture;
        this.completed = new CompletableFuture<>();
        this.registryResourceKey = registryResourceKey;
    }

    protected abstract void addTags(HolderLookup.Provider lookupProvider);

    public ClientRegistryTagAppender<T> clientRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey) {
        return new ClientRegistryTagAppender<>(getOrCreateBuilder(clientRegistryTagKey.getTagId()), registryResourceKey);
    }

    public ClientRegistryTagAppender.RegistryAware<T> clientRegistryTag(ClientRegistryTagKey<T> clientRegistryTagKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryTagAppender.RegistryAware<>(getOrCreateBuilder(clientRegistryTagKey.getTagId()), registryResourceKey, lookupProvider);
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
                    HolderLookup.RegistryLookup<T> registryLookup = provider.lookup(registryResourceKey).orElseThrow(() -> {
                        if (RegistryManager.ACTIVE.getRegistry(this.registryResourceKey) != null) {
                            return new IllegalStateException("Forge registry " + this.registryResourceKey.location() + " does not have support for tags");
                        }
                        return new IllegalStateException("Vanilla registry " + registryResourceKey.location() + " is not present.");
                    });
                    Predicate<ResourceLocation> elementPredicate = elementLocation -> {
                        return registryLookup.get(ResourceKey.create(registryResourceKey, elementLocation)).isPresent();
                    };

                    return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
                        List<TagEntry> tagEntries = entry.getValue().build();
                        List<TagEntry> errors = tagEntries.stream().filter(tagEntry -> {
                            return !elementPredicate.test(tagEntry.getId()) && !doesTagExist(tagEntry.getId());
                        }).toList();
                        if (!errors.isEmpty()) {
                            throw new IllegalStateException("Tag entries [%s] were not found for registry %s".formatted(errors.stream().map(Object::toString).collect(Collectors.joining(",")), registryResourceKey));
                        } else {
                            DataResult<JsonElement> jsonResult = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(entry.getValue().build(), entry.getValue().isReplace()));
                            JsonElement json = jsonResult.getOrThrow(false, Trimmed.LOGGER::error);
                            Path filePath = pathProvider.json(entry.getKey());
                            return DataProvider.saveStable(pOutput, json, filePath);
                        }
                    }).toArray(CompletableFuture[]::new));
                });
    }

    protected boolean doesTagExist(ResourceLocation clientTagLocation) {
        return builders.get(clientTagLocation) != null || existingFileHelper.exists(clientTagLocation, resourceType);
    }

    @Override
    public String getName() {
        return "ClientRegistryTagProvider<" + registryResourceKey.location() + "> for " + modid;
    }
}
