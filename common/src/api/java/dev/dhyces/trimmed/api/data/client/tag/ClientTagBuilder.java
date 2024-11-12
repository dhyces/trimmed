package dev.dhyces.trimmed.api.data.client.tag;

import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;
import net.minecraft.resources.ResourceLocation;

import java.util.Set;

public class ClientTagBuilder<T> {
    private final Set<ClientTagEntry> elements;
    private boolean replaces;

    public ClientTagBuilder() {
        this.elements = new ObjectLinkedOpenHashSet<>();
    }

    public ClientTagBuilder<T> add(ResourceLocation element, boolean isRequired) {
        this.elements.add(ClientTagEntry.element(element, isRequired));
        return this;
    }

    public ClientTagBuilder<T> addTag(ClientTagKey<T> clientTagKey, boolean isRequired) {
        this.elements.add(ClientTagEntry.clientTagKey(clientTagKey, isRequired));
        return this;
    }

    public ClientTagBuilder<T> setReplaces(boolean doesReplace) {
        this.replaces = doesReplace;
        return this;
    }

    public ClientTagFile build() {
        return new ClientTagFile(elements, replaces);
    }
}
