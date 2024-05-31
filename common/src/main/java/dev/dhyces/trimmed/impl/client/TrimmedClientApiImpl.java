package dev.dhyces.trimmed.impl.client;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public class TrimmedClientApiImpl implements TrimmedClientApi {
    public static final TrimmedClientApi INSTANCE = new TrimmedClientApiImpl();

    @Nullable
    @Override
    public <T> KeyResolver<T> getKeyResolver(ResourceLocation id) {
        return KeyResolvers.getResolver(id);
    }

    @Nullable
    @Override
    public <T> KeyResolver.RegistryWrapper<T> getRegistryKeyResolver(ResourceKey<? extends Registry<T>> key) {
        return KeyResolvers.getRegistryResolver(key);
    }

    @Nullable
    @Override
    public <T> ResourceLocation getId(KeyResolver<T> keyResolver) {
        return KeyResolvers.getId(keyResolver);
    }
}
