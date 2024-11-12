package dev.dhyces.trimmed.api.data.client.tag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.ExtraCodecs;

import java.util.function.Predicate;

public record ClientTagEntry(ExtraCodecs.TagOrElementLocation tagOrElementLocation, boolean isRequired) {
    public static final Codec<ClientTagEntry> FULL_CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ExtraCodecs.TAG_OR_ELEMENT_ID.fieldOf("id").forGetter(ClientTagEntry::tagOrElementLocation),
                    Codec.BOOL.fieldOf("required").forGetter(ClientTagEntry::isRequired)
            ).apply(instance, ClientTagEntry::new)
    );

    public static final Codec<ClientTagEntry> CODEC = Codec.withAlternative(
            ExtraCodecs.TAG_OR_ELEMENT_ID.xmap(
                    either -> new ClientTagEntry(either, true),
                    ClientTagEntry::tagOrElementLocation
            ),
            FULL_CODEC
    );

    public boolean isTag() {
        return tagOrElementLocation.tag();
    }

    public ResourceLocation getId() {
        return tagOrElementLocation.id();
    }

    public <T> ClientTagKey<T> getTag(KeyResolver<T> keyResolver) {
        return ClientTagKey.of(keyResolver, tagOrElementLocation.id());
    }

    public boolean verifyExists(Predicate<ResourceLocation> elementPredicate, Predicate<ResourceLocation> tagPredicate) {
        if (tagOrElementLocation.tag()) {
            return tagPredicate.test(tagOrElementLocation.id());
        }
        return elementPredicate.test(tagOrElementLocation.id());
    }

    public static ClientTagEntry element(ResourceLocation element, boolean isRequired) {
        return new ClientTagEntry(new ExtraCodecs.TagOrElementLocation(element, false), isRequired);
    }

    public static <T> ClientTagEntry clientTagKey(ClientTagKey<T> clientTagKey, boolean isRequired) {
        return new ClientTagEntry(new ExtraCodecs.TagOrElementLocation(clientTagKey.getTagId(), true), isRequired);
    }
}