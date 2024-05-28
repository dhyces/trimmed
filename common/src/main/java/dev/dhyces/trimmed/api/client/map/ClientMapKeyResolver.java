package dev.dhyces.trimmed.api.client.map;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.maps.MapKeyResolver;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public record ClientMapKeyResolver<T>(Codec<T> codec) implements MapKeyResolver<T> {

    @Override
    public Codec<T> getCodec() {
        return codec;
    }

    @Override
    public @Nullable StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
        return null;
    }
}
