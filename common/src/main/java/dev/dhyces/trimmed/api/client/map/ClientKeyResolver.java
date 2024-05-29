package dev.dhyces.trimmed.api.client.map;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.maps.KeyResolver;
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
}
