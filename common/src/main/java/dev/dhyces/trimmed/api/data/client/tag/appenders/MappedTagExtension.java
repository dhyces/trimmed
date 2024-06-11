package dev.dhyces.trimmed.api.data.client.tag.appenders;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.function.Function;

public interface MappedTagExtension<T, S extends ClientTagAppender<T>> {
    Function<T, @Nullable ResourceLocation> getEncoder();
    S getSelf();

    default S add(T element) {
        ResourceLocation encoded = getEncoder().apply(element);
        if (encoded == null) {
            throw new IllegalArgumentException("Encoder could not map key to resource location");
        }
        getSelf().add(encoded);
        return getSelf();
    }

    default S add(T... elements) {
        for (T element : elements) {
            ResourceLocation encoded = getEncoder().apply(element);
            if (encoded == null) {
                throw new IllegalArgumentException("Encoder could not map key to resource location");
            }
            getSelf().add(encoded);
        }
        return getSelf();
    }

    default S addOptional(T element) {
        ResourceLocation encoded = getEncoder().apply(element);
        if (encoded == null) {
            throw new IllegalArgumentException("Encoder could not map key to resource location");
        }
        getSelf().addOptional(encoded);
        return getSelf();
    }

    default S addOptional(T... elements) {
        for (T element : elements) {
            ResourceLocation encoded = getEncoder().apply(element);
            if (encoded == null) {
                throw new IllegalArgumentException("Encoder could not map key to resource location");
            }
            getSelf().addOptional(encoded);
        }
        return getSelf();
    }
}
