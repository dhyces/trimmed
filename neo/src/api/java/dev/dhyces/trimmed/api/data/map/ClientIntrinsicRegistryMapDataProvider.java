package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.data.map.appenders.RegistryMapAppender;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientIntrinsicRegistryMapDataProvider<K> extends ClientRegistryMapDataProvider<K> {
    protected final Function<K, @Nullable ResourceLocation> encoder;

    public ClientIntrinsicRegistryMapDataProvider(PackOutput packOutput, String modid, ResourceKey<? extends Registry<K>> registryKey, Function<K, @Nullable ResourceLocation> encoder, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, registryKey, lookupProviderFuture, existingFileHelper);
        this.encoder = encoder;
    }

    public <V> RegistryMapAppender.Mapped<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider) {
        return new RegistryMapAppender.Mapped<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(registryKey), encoder);
    }
}