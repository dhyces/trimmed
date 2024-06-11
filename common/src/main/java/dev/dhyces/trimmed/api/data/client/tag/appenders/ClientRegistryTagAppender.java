package dev.dhyces.trimmed.api.data.client.tag.appenders;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagBuilder;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;
import java.util.function.Supplier;

public class ClientRegistryTagAppender<T> extends ClientTagAppender<T> {
    private final HolderLookup.RegistryLookup<T> lookup;

    public ClientRegistryTagAppender(ClientTagBuilder<T> builder, HolderLookup.RegistryLookup<T> lookup) {
        super(builder);
        this.lookup = lookup;
    }

    public ClientRegistryTagAppender<T> add(ResourceKey<T> element) {
        if (!element.isFor(lookup.key())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + lookup.key() + "!");
        }
        add(element.location());
        return this;
    }

    public ClientRegistryTagAppender<T> add(Holder<T> holder) {
        if (!holder.unwrapKey().orElseThrow().isFor(lookup.key())) {
            throw new IllegalArgumentException("Element " + holder + " is not valid in current registry set");
        }
        return add(holder.unwrapKey().orElseThrow());
    }

    public ClientRegistryTagAppender<T> addTag(ClientTagKey<T> tagKey) {
        if (!(tagKey.getKeyResolver() instanceof KeyResolver.RegistryResolver<T> registryResolver) || registryResolver.getKey() != lookup.key()) {
            throw new IllegalArgumentException("TagKey " + tagKey + " is not for registry " + lookup.key() + "!");
        }
        return addTag(tagKey);
    }

    public ClientRegistryTagAppender<T> addOptional(ResourceKey<T> element) {
        if (!element.isFor(lookup.key())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + lookup.key() + "!");
        }
        addOptional(element.location());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(Holder<T> holder) {
        if (!holder.unwrapKey().orElseThrow().isFor(lookup.key())) {
            throw new IllegalArgumentException("Element " + holder + " is not valid in current registry set");
        }
        return addOptional(holder.unwrapKey().orElseThrow());
    }

    public ClientRegistryTagAppender<T> addOptionalTag(ClientTagKey<T> tagKey) {
        if (!(tagKey.getKeyResolver() instanceof KeyResolver.RegistryResolver<T> registryResolver) || registryResolver.getKey() != lookup.key()) {
            throw new IllegalArgumentException("TagKey " + tagKey + " is not for registry " + lookup.key() + "!");
        }
        return addOptionalTag(tagKey);
    }

    public static class Mapped<T> extends ClientRegistryTagAppender<T> implements MappedTagExtension<T, Mapped<T>> {
        protected final Function<T, @Nullable ResourceLocation> encoder;

        public Mapped(ClientTagBuilder<T> builder, HolderLookup.RegistryLookup<T> lookup, Function<T, @Nullable ResourceLocation> encoder) {
            super(builder, lookup);
            this.encoder = encoder;
        }

        @Override
        public Function<T, @Nullable ResourceLocation> getEncoder() {
            return encoder;
        }

        @Override
        public ClientRegistryTagAppender.Mapped<T> getSelf() {
            return this;
        }

        public ClientRegistryTagAppender<T> add(Supplier<T> element) {
            add(element.get());
            return this;
        }

        public ClientRegistryTagAppender<T> addOptional(Supplier<T> element) {
            addOptional(element.get());
            return this;
        }
    }
}
