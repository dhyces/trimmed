package dev.dhyces.trimmed.api;

import com.mojang.serialization.DynamicOps;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

public interface KeyResolver<T> {
    @Nullable
    T decode(ResourceLocation resourceLocation, DynamicOps<?> ops);
    @Nullable
    StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec();
    boolean requiresActiveWorld();

    interface RegistryResolver<T> extends KeyResolver<T> {
        ResourceKey<? extends Registry<T>> getKey();
    }

    record Dynamic<T>(ResourceKey<? extends Registry<T>> registryKey) implements RegistryResolver<T> {

        @Override
        public ResourceKey<? extends Registry<T>> getKey() {
            return registryKey;
        }

        @Nullable
        @Override
        public T decode(ResourceLocation resourceLocation, DynamicOps<?> ops) {
            if (!(ops instanceof RegistryOps<?> registryOps)) {
                throw new IllegalArgumentException("Must have registry ops to decode dynamic content.");
            }
            return registryOps.getter(registryKey).orElseThrow(() -> new IllegalStateException("Registry {%s} is not available".formatted(registryKey.location())))
                    .get(ResourceKey.create(registryKey, resourceLocation))
                    .map(Holder.Reference::value)
                    .orElse(null);
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
            return ByteBufCodecs.registry(registryKey);
        }

        @Override
        public boolean requiresActiveWorld() {
            return true;
        }
    }

    record Static<T>(Registry<T> registry) implements RegistryResolver<T> {

        @Override
        public ResourceKey<? extends Registry<T>> getKey() {
            return registry.key();
        }

        @Override
        public @Nullable T decode(ResourceLocation resourceLocation, DynamicOps<?> ops) {
            return registry.get(resourceLocation);
        }

        @Override
        public StreamCodec<RegistryFriendlyByteBuf, T> getStreamCodec() {
            return ByteBufCodecs.registry(registry.key());
        }

        @Override
        public boolean requiresActiveWorld() {
            return false;
        }
    }
}
