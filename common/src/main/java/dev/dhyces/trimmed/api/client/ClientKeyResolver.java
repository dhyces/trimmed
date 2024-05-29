package dev.dhyces.trimmed.api.client;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public record ClientKeyResolver<T>(Codec<T> codec) implements KeyResolver<T> {

    @Override
    public Codec<T> getCodec() {
        return codec;
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
