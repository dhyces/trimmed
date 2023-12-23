package dev.dhyces.trimmed.api.data.tags.appenders;

import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

public class ClientTagAppender {
    private final TagBuilder backed;
    private final String namespace;

    public ClientTagAppender(String namespace, TagBuilder builder) {
        this.backed = builder;
        this.namespace = namespace;
    }

    public ClientTagAppender add(String element) {
        backed.addElement(new ResourceLocation(namespace, element));
        return this;
    }

    public ClientTagAppender add(String... elements) {
        for (String s : elements) {
            backed.addElement(new ResourceLocation(namespace, s));
        }
        return this;
    }

    public ClientTagAppender add(ResourceLocation element) {
        backed.addElement(element);
        return this;
    }

    public ClientTagAppender add(ResourceLocation... elements) {
        for (ResourceLocation elem : elements) {
            backed.addElement(elem);
        }
        return this;
    }

    public ClientTagAppender addTag(ClientTagKey tagKey) {
        backed.addTag(tagKey.getTagId());
        return this;
    }

    public ClientTagAppender addTags(ClientTagKey... tagKeys) {
        for (ClientTagKey key : tagKeys) {
            backed.addTag(key.getTagId());
        }
        return this;
    }

    public ClientTagAppender addOptional(String element) {
        backed.addOptionalElement(new ResourceLocation(namespace, element));
        return this;
    }

    public ClientTagAppender addOptional(String... elements) {
        for (String elem : elements) {
            backed.addOptionalElement(new ResourceLocation(namespace, elem));
        }
        return this;
    }

    public ClientTagAppender addOptional(ResourceLocation element) {
        backed.addOptionalElement(element);
        return this;
    }

    public ClientTagAppender addOptional(ResourceLocation... elements) {
        for (ResourceLocation elem : elements) {
            backed.addOptionalElement(elem);
        }
        return this;
    }

    public ClientTagAppender addOptionalTag(ClientTagKey tagKey) {
        backed.addOptionalTag(tagKey.getTagId());
        return this;
    }

    public ClientTagAppender addOptionalTags(ClientTagKey... tagKeys) {
        for (ClientTagKey key : tagKeys) {
            backed.addOptionalTag(key.getTagId());
        }
        return this;
    }
}
