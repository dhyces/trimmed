package dev.dhyces.trimmed.api.client;

import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface TrimmedClientApi {
    static TrimmedClientApi getInstance() {
        throw new AssertionError("Implemented with Mixin");
    }

    @Nullable
    <T> KeyResolver<T> getKeyResolver(ResourceLocation id);
    @Nullable
    <T> KeyResolver.RegistryResolver<T> getRegistryKeyResolver(ResourceKey<? extends Registry<T>> key);
    @Nullable
    <T> ResourceLocation getId(KeyResolver<T> keyResolver);
}
