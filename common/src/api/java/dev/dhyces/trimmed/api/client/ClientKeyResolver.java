package dev.dhyces.trimmed.api.client;

import com.mojang.serialization.DynamicOps;
import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiFunction;

public record ClientKeyResolver<T>(BiFunction<ResourceLocation, DynamicOps<?>, T> decoder) implements KeyResolver<T> {
    @Override
    public @Nullable T decode(ResourceLocation resourceLocation, DynamicOps<?> ops) {
        return decoder.apply(resourceLocation, ops);
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
        return null;
    }

    @Override
    public boolean requiresActiveWorld() {
        return false;
    }
}
