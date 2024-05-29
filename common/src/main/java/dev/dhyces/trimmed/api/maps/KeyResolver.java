package dev.dhyces.trimmed.api.maps;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import org.jetbrains.annotations.Nullable;

public interface KeyResolver<T> {
    Codec<T> getCodec();
    @Nullable
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    boolean requiresActiveWorld();

    record RegistryWrapper<T>(Registry<T> registry, boolean requiresActiveWorld) implements KeyResolver<T> {

        @Override
        public Codec<T> getCodec() {
            return registry.byNameCodec();
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
            return ByteBufCodecs.registry(registry.key());
        }

        @Override
        public boolean requiresActiveWorld() {
            return requiresActiveWorld;
        }
    }
}
