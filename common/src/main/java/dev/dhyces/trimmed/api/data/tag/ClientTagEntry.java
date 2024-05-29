package dev.dhyces.trimmed.api.data.tag;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import org.jetbrains.annotations.Nullable;

import java.util.function.Predicate;

public record ClientTagEntry<T>(Either<T, ClientTagKey<T>> element, boolean isRequired) {
    public static <T> Codec<Either<T, ClientTagKey<T>>> elementOrTagCodec(KeyResolver<T> keyResolver) {
        return Codec.either(keyResolver.getCodec(), ClientTagKey.tagCodec(keyResolver));
    }
    public static <T> Codec<ClientTagEntry<T>> fullCodec(KeyResolver<T> keyResolver) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        elementOrTagCodec(keyResolver).fieldOf("value").forGetter(ClientTagEntry::element),
                        Codec.BOOL.fieldOf("required").forGetter(ClientTagEntry::isRequired)
                ).apply(instance, ClientTagEntry::new)
        );
    }
    public static <T> Codec<ClientTagEntry<T>> codec(KeyResolver<T> keyResolver) {
        return Codec.withAlternative(elementOrTagCodec(keyResolver).xmap(
                either -> new ClientTagEntry<>(either, true),
                ClientTagEntry::element), fullCodec(keyResolver)
        );
    }

    public boolean isTag() {
        return element.right().isPresent();
    }

    @Nullable
    public T getElement() {
        return element.left().orElse(null);
    }

    @Nullable
    public ClientTagKey<T> getTag() {
        return element.right().orElse(null);
    }

    public boolean verifyExists(Predicate<T> elementPredicate, Predicate<ClientTagKey<T>> keyPredicate) {
        return element.map(elementPredicate::test, keyPredicate::test);
    }

    public static <T> ClientTagEntry<T> element(T element, boolean isRequired) {
        return new ClientTagEntry<>(Either.left(element), isRequired);
    }

    public static <T> ClientTagEntry<T> clientTagKey(ClientTagKey<T> clientTagKey, boolean isRequired) {
        return new ClientTagEntry<>(Either.right(clientTagKey), isRequired);
    }
}