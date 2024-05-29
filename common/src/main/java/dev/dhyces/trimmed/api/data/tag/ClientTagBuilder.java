package dev.dhyces.trimmed.api.data.tag;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import it.unimi.dsi.fastutil.objects.ObjectLinkedOpenHashSet;

import java.util.Set;

public class ClientTagBuilder<T> {
    private final KeyResolver<T> keyResolver;
    private final Set<ClientTagEntry<T>> elements;
    private boolean replaces;

    public ClientTagBuilder(KeyResolver<T> keyResolver) {
        this.keyResolver = keyResolver;
        this.elements = new ObjectLinkedOpenHashSet<>();
    }

    public ClientTagBuilder<T> add(T element, boolean isRequired) {
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

    public ClientTagFile<T> build() {
        return new ClientTagFile<>(elements, replaces);
    }
}
