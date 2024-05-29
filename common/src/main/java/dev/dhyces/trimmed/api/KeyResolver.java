package dev.dhyces.trimmed.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

public interface KeyResolver<T> {
    Codec<T> getCodec();
    @Nullable
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    boolean requiresActiveWorld();

    record RegistryWrapper<T>(ResourceKey<? extends Registry<T>> registryKey, Codec<T> byNameCodec, boolean requiresActiveWorld) implements KeyResolver<T> {
        public static <T> RegistryWrapper<T> createStatic(Registry<T> registry) {
            return new RegistryWrapper<>(registry.key(), registry.byNameCodec(), false);
        }


        @Override
        public Codec<T> getCodec() {
            return byNameCodec;
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
            return ByteBufCodecs.registry(registryKey);
        }

        @Override
        public boolean requiresActiveWorld() {
            return requiresActiveWorld;
        }
    }
}
