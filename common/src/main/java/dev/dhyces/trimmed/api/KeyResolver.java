package dev.dhyces.trimmed.api;

import com.mojang.serialization.Codec;
import net.minecraft.core.Holder;
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

    interface RegistryResolver<T> extends KeyResolver<T> {
        ResourceKey<? extends Registry<T>> getKey();
    }

    record RegistryWrapper<T>(ResourceKey<? extends Registry<T>> registryKey, Codec<Holder<T>> holderByNameCodec, boolean requiresActiveWorld) implements RegistryResolver<T> {
        public static <T> RegistryWrapper<T> createStatic(Registry<T> registry) {
            return new RegistryWrapper<>(registry.key(), registry.holderByNameCodec(), false);
        }

        @Override
        public Codec<T> getCodec() {
            return holderByNameCodec.xmap(Holder::value, t -> {throw new UnsupportedOperationException("Does not support encoding values");});
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
            return ByteBufCodecs.registry(registryKey);
        }

        @Override
        public boolean requiresActiveWorld() {
            return requiresActiveWorld;
        }

        @Override
        public ResourceKey<? extends Registry<T>> getKey() {
            return registryKey;
        }
    }
}
