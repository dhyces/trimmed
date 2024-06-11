package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.impl.client.TrimmedClientApiImpl;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface TrimmedClientApi {
    static TrimmedClientApi getInstance() {
        return TrimmedClientApiImpl.INSTANCE;
    }

    @Nullable
    <T> KeyResolver<T> getKeyResolver(ResourceLocation id);
    @Nullable
    <T> KeyResolver.RegistryResolver<T> getRegistryKeyResolver(ResourceKey<? extends Registry<T>> key);
    @Nullable
    <T> ResourceLocation getId(KeyResolver<T> keyResolver);
}
