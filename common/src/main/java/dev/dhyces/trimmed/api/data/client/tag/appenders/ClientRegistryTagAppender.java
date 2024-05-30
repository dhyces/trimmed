package dev.dhyces.trimmed.api.data.client.tag.appenders;

import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagBuilder;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;
import java.util.function.Supplier;

public class ClientRegistryTagAppender<T> {
    private final ClientTagBuilder<T> builder;
    private final HolderLookup.RegistryLookup<T> lookup;
    private final Map<T, ResourceLocation> reverseLookup;

    public ClientRegistryTagAppender(ClientTagBuilder<T> builder, HolderLookup.RegistryLookup<T> lookup) {
        this.builder = builder;
        this.lookup = lookup;
        this.reverseLookup = lookup.listElements().map(tReference -> Map.entry(tReference.value(), tReference.key().location())).collect(Util.toMap());
    }

    protected T lookupOrThrow(ResourceKey<T> key) {
        return lookup.getOrThrow(key).value();
    }

    public ClientRegistryTagAppender<T> add(T element, boolean isRequired) {
        if (!reverseLookup.containsKey(element)) {
            throw new IllegalArgumentException("Element is not a member of the registry \"" + lookup.key() + "\"");
        }
        builder.add(element, isRequired);
        return this;
    }

    public ClientRegistryTagAppender<T> add(T element) {
        add(element, true);
        return this;
    }

    public ClientRegistryTagAppender<T> add(Supplier<T> element) {
        add(element.get());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(T element) {
        addOptional(element);
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(Supplier<T> element) {
        addOptional(element.get());
        return this;
    }

    public ClientRegistryTagAppender<T> add(ResourceKey<T> element) {
        if (!element.registry().equals(lookup.key().location())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + lookup.key() + "!");
        }
        add(lookupOrThrow(element));
        return this;
    }

    public ClientRegistryTagAppender<T> addTag(ClientTagKey<T> tagKey, boolean isRequired) {
        if (!tagKey.getTagId().equals(lookup.key().location())) {
            throw new IllegalArgumentException("TagKey " + tagKey + " is not for registry " + lookup.key() + "!");
        }
        builder.addTag(tagKey, isRequired);
        return this;
    }

    public ClientRegistryTagAppender<T> addTag(ClientTagKey<T> tagKey) {
        addTag(tagKey, true);
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(ResourceKey<T> element) {
        add(lookupOrThrow(element), false);
        return this;
    }

    public ClientRegistryTagAppender<T> addOptionalTag(ClientTagKey<T> optionalTagKey) {
        addTag(optionalTagKey, false);
        return this;
    }
}
