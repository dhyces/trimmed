package dhyces.trimmed.api.data.tags.appenders;

import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ClientRegistryTagAppender<T> {
    private final TagBuilder backed;
    private final ResourceKey<? extends Registry<T>> resourceKey;

    public ClientRegistryTagAppender(TagBuilder builder, ResourceKey<? extends Registry<T>> registryResourceKey) {
        this.backed = builder;
        this.resourceKey = registryResourceKey;
    }

    public ClientRegistryTagAppender<T> add(ResourceLocation element) {
        backed.addElement(element);
        return this;
    }

    public ClientRegistryTagAppender<T> add(ResourceKey<T> element) {
        if (!element.registry().equals(resourceKey.location())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + resourceKey + "!");
        }
        backed.addElement(element.location());
        return this;
    }

    public ClientRegistryTagAppender<T> addTag(ClientRegistryTagKey<T> tagKey) {
        if (!tagKey.getRegistryKey().equals(resourceKey)) {
            throw new IllegalArgumentException("TagKey " + tagKey + " is not for registry " + resourceKey + "!");
        }
        backed.addTag(tagKey.getTagId());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(ResourceLocation optionalElement) {
        backed.addOptionalElement(optionalElement);
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(ResourceKey<T> element) {
        if (!element.registry().equals(resourceKey.location())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + resourceKey + "!");
        }
        backed.addOptionalElement(element.location());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptionalTag(ClientRegistryTagKey<T> optionalTagKey) {
        if (!optionalTagKey.getRegistryKey().equals(resourceKey)) {
            throw new IllegalArgumentException("TagKey " + optionalTagKey + " is not for registry " + resourceKey + "!");
        }
        backed.addOptionalTag(optionalTagKey.getTagId());
        return this;
    }

    public static final class RegistryAware<T> extends ClientRegistryTagAppender<T> {
        private final Map<T, ResourceLocation> lookup;

        public RegistryAware(TagBuilder builder, ResourceKey<? extends Registry<T>> registryResourceKey, HolderLookup.Provider lookupProvider) {
            super(builder, registryResourceKey);
            this.lookup = lookupProvider.lookupOrThrow(registryResourceKey).listElements().map(tReference -> Map.entry(tReference.value(), tReference.key().location())).collect(Util.toMap());
        }

        public RegistryAware<T> add(T element) {
            add(lookup.get(element));
            return this;
        }

        public RegistryAware<T> add(Supplier<T> element) {
            add(element.get());
            return this;
        }

        public RegistryAware<T> add(ResourceLocation element) {
            super.add(element);
            return this;
        }

        public RegistryAware<T> addTag(ClientRegistryTagKey<T> tagKey) {
            super.addTag(tagKey);
            return this;
        }

        public RegistryAware<T> addOptional(T element) {
            addOptional(lookup.get(element));
            return this;
        }

        public RegistryAware<T> addOptional(ResourceLocation optionalElement) {
            super.addOptional(optionalElement);
            return this;
        }

        public RegistryAware<T> addOptionalTag(ClientRegistryTagKey<T> optionalTagKey) {
            super.addOptionalTag(optionalTagKey);
            return this;
        }
    }
}
