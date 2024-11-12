package dev.dhyces.trimmed.api.client;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;

public interface TrimmedClientTagApi {
    static TrimmedClientTagApi getInstance() {
        throw new AssertionError("Implemented with Mixin");
    }

    <T> TagHolder<T> getTag(ClientTagKey<T> clientTagKey);

    <T> Codec<TagHolder<T>> tagCodec(KeyResolver<T> keyResolver);
}
