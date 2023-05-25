package dhyces.trimmed.api.data.tags.appenders;

import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

public class ClientTagAppender {
    private final TagBuilder backed;

    public ClientTagAppender(TagBuilder builder) {
        this.backed = builder;
    }

    public ClientTagAppender add(ResourceLocation element) {
        backed.addElement(element);
        return this;
    }

    public ClientTagAppender addTag(ClientTagKey tagKey) {
        backed.addTag(tagKey.getTagId());
        return this;
    }

    public ClientTagAppender addOptional(ResourceLocation element) {
        backed.addOptionalElement(element);
        return this;
    }

    public ClientTagAppender addOptionalTag(ClientTagKey tagKey) {
        backed.addOptionalTag(tagKey.getTagId());
        return this;
    }
}
