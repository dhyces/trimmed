package dev.dhyces.trimmed.api.data.client.tag.appenders;

import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagBuilder;

public class ClientTagAppender<T> {
    private final ClientTagBuilder<T> builder;

    public ClientTagAppender(ClientTagBuilder<T> builder) {
        this.builder = builder;
    }

    public ClientTagAppender<T> add(T element) {
        builder.add(element, true);
        return this;
    }

    @SafeVarargs
    public final ClientTagAppender<T> add(T... elements) {
        for (T element : elements) {
            builder.add(element, true);
        }
        return this;
    }

    public ClientTagAppender<T> addTag(ClientTagKey<T> tagKey) {
        builder.addTag(tagKey, true);
        return this;
    }

    @SafeVarargs
    public final ClientTagAppender<T> addTags(ClientTagKey<T>... tagKeys) {
        for (ClientTagKey<T> key : tagKeys) {
            builder.addTag(key, true);
        }
        return this;
    }

    public ClientTagAppender<T> addOptional(T element) {
        builder.add(element, false);
        return this;
    }

    @SafeVarargs
    public final ClientTagAppender<T> addOptional(T... elements) {
        for (T elem : elements) {
            builder.add(elem, false);
        }
        return this;
    }

    public ClientTagAppender<T> addOptionalTag(ClientTagKey<T> tagKey) {
        builder.addTag(tagKey, false);
        return this;
    }

    @SafeVarargs
    public final ClientTagAppender<T> addOptionalTags(ClientTagKey<T>... tagKeys) {
        for (ClientTagKey<T> key : tagKeys) {
            builder.addTag(key, false);
        }
        return this;
    }
}
