package dhyces.trimmed.api.data.tags.appenders;

import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

public class ClientTagAppender {
    private final TagBuilder backed;
    private final String modid;

    public ClientTagAppender(String modid, TagBuilder builder) {
        this.backed = builder;
        this.modid = modid;
    }

    public ClientTagAppender add(String element) {
        backed.addElement(new ResourceLocation(modid, element));
        return this;
    }

    public ClientTagAppender add(String... elements) {
        for (String s : elements) {
            backed.addElement(new ResourceLocation(modid, s));
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

    public ClientTagAppender addOptional(String element) {
        backed.addOptionalElement(new ResourceLocation(modid, element));
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
