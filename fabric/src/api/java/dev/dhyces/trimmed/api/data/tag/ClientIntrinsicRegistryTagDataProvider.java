package dev.dhyces.trimmed.api.data.tag;

import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.appenders.ClientRegistryTagAppender;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientIntrinsicRegistryTagDataProvider<T> extends ClientRegistryTagDataProvider<T> {
    protected final Function<T, @Nullable ResourceLocation> encoder;

    public ClientIntrinsicRegistryTagDataProvider(PackOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<T>> registryKey, Function<T, @Nullable ResourceLocation> encoder) {
        super(packOutput, modid, lookupProviderFuture, registryKey);
        this.encoder = encoder;
    }

    public ClientRegistryTagAppender.Mapped<T> tag(ClientTagKey<T> clientTagKey, HolderLookup.Provider lookupProvider) {
        return new ClientRegistryTagAppender.Mapped<>(getOrCreateBuilder(clientTagKey), lookupProvider.lookupOrThrow(keyResolver.getKey()), encoder);
    }
}