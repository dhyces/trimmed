package dev.dhyces.trimmed.api.client.tag;

import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;

public interface TagHolder<T> {
    default ClientTagKey<T> unwrapKeyOrThrow() {
        return getKey().orElseThrow(() -> new IllegalStateException("No key is present for map holder"));
    }
    default Optional<ClientTagKey<T>> getKey() {
        return Optional.ofNullable(unwrapKey());
    }
    @Nullable
    ClientTagKey<T> unwrapKey();
    Set<T> getSet();
    default boolean contains(T element) {
        if (!isBound()) {
            return false;
        }
        return getSet().contains(element);
    }
    boolean isRequired(T element);
    boolean isBound();

    static <T> TagHolder<T> simpleWrapper(Set<T> set) {
        return new TagHolder<>() {
            @Override
            public @Nullable ClientTagKey<T> unwrapKey() {
                return null;
            }

            @Override
            public Set<T> getSet() {
                return set;
            }

            @Override
            public boolean contains(T element) {
                return set.contains(element);
            }

            @Override
            public boolean isRequired(T element) {
                return true;
            }

            @Override
            public boolean isBound() {
                return true;
            }
        };
    }
}
