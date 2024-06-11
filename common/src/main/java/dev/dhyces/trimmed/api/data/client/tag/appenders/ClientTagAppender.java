package dev.dhyces.trimmed.api.data.client.tag.appenders;

import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagBuilder;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public class ClientTagAppender<T> {
    protected final ClientTagBuilder<T> builder;

    public ClientTagAppender(ClientTagBuilder<T> builder) {
        this.builder = builder;
    }

    public ClientTagAppender<T> add(ResourceLocation element) {
        builder.add(element, true);
        return this;
    }

    public final ClientTagAppender<T> add(ResourceLocation... elements) {
        for (ResourceLocation element : elements) {
            add(element);
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
            addTag(key);
        }
        return this;
    }

    public ClientTagAppender<T> addOptional(ResourceLocation element) {
        builder.add(element, false);
        return this;
    }

    public final ClientTagAppender<T> addOptional(ResourceLocation... elements) {
        for (ResourceLocation elem : elements) {
            addOptional(elem);
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
            addOptionalTag(key);
        }
        return this;
    }

    public static class Mapped<T> extends ClientTagAppender<T> implements MappedTagExtension<T, Mapped<T>> {
        protected final Function<T, @Nullable ResourceLocation> encoder;

        public Mapped(ClientTagBuilder<T> builder, Function<T, @Nullable ResourceLocation> encoder) {
            super(builder);
            this.encoder = encoder;
        }

        @Override
        public Function<T, @Nullable ResourceLocation> getEncoder() {
            return encoder;
        }

        @Override
        public Mapped<T> getSelf() {
            return this;
        }
    }
}
