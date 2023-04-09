package dhyces.trimmed.api.data.tags.appenders;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagBuilder;

public class ClientTagAppender {
    private final TagBuilder backed;
    private final String modid;

    public ClientTagAppender(TagBuilder builder, String modid) {
        this.backed = builder;
        this.modid = modid;
    }

    public ClientTagAppender add(ResourceLocation element) {
        backed.addElement(element);
        return this;
    }
}
