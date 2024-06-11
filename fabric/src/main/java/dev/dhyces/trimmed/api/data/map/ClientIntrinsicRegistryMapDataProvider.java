package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.data.map.appenders.RegistryMapAppender;
import dev.dhyces.trimmed.api.maps.MapKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientIntrinsicRegistryMapDataProvider<K> extends ClientRegistryMapDataProvider<K> {
    protected final Function<K, @Nullable ResourceLocation> encoder;

    public ClientIntrinsicRegistryMapDataProvider(FabricDataOutput packOutput, String modid, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ResourceKey<? extends Registry<K>> registryKey, Function<K, @Nullable ResourceLocation> encoder) {
        super(packOutput, modid, lookupProviderFuture, registryKey);
        this.encoder = encoder;
    }

    public <V> RegistryMapAppender.Mapped<K, V> map(MapKey<K, V> mapKey, HolderLookup.Provider lookupProvider) {
        return new RegistryMapAppender.Mapped<>(getOrCreateBuilder(mapKey), lookupProvider.lookupOrThrow(registryKey), encoder);
    }
}
