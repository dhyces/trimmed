package dev.dhyces.trimmed.api.data.client.tag.appenders;

import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

import java.util.Map;
import java.util.function.Supplier;

public class ClientRegistryTagAppender<T> {
    private final TagBuilder backed;
    private final HolderLookup.RegistryLookup<T> lookup;
    private final Map<T, ResourceLocation> reverseLookup;

    public ClientRegistryTagAppender(TagBuilder builder, HolderLookup.RegistryLookup<T> lookup) {
        this.backed = builder;
        this.lookup = lookup;
        this.reverseLookup = lookup.listElements().map(tReference -> Map.entry(tReference.value(), tReference.key().location())).collect(Util.toMap());
    }

    public ClientRegistryTagAppender<T> add(T element) {
        add(reverseLookup.get(element));
        return this;
    }

    public ClientRegistryTagAppender<T> add(Supplier<T> element) {
        add(element.get());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(T element) {
        addOptional(reverseLookup.get(element));
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(Supplier<T> element) {
        addOptional(element.get());
        return this;
    }

    public ClientRegistryTagAppender<T> add(ResourceLocation element) {
        backed.addElement(element);
        return this;
    }

    public ClientRegistryTagAppender<T> add(ResourceKey<T> element) {
        if (!element.registry().equals(lookup.key().location())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + lookup.key() + "!");
        }
        backed.addElement(element.location());
        return this;
    }

    public ClientRegistryTagAppender<T> addTag(ClientTagKey<T> tagKey) {
        if (!tagKey.getTagId().equals(lookup.key().location())) {
            throw new IllegalArgumentException("TagKey " + tagKey + " is not for registry " + lookup.key() + "!");
        }
        backed.addTag(tagKey.getTagId());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(ResourceLocation optionalElement) {
        backed.addOptionalElement(optionalElement);
        return this;
    }

    public ClientRegistryTagAppender<T> addOptional(ResourceKey<T> element) {
        if (!element.registry().equals(lookup.key().location())) {
            throw new IllegalArgumentException("Element " + element.location() + " is not for registry " + lookup.key() + "!");
        }
        backed.addOptionalElement(element.location());
        return this;
    }

    public ClientRegistryTagAppender<T> addOptionalTag(ClientTagKey<T> optionalTagKey) {
        if (!optionalTagKey.getTagId().equals(lookup.key().location())) {
            throw new IllegalArgumentException("TagKey " + optionalTagKey + " is not for registry " + lookup.key() + "!");
        }
        backed.addOptionalTag(optionalTagKey.getTagId());
        return this;
    }
}
