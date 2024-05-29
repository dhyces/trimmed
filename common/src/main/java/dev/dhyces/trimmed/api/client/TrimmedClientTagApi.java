package dev.dhyces.trimmed.api.client;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.maps.KeyResolver;
import dev.dhyces.trimmed.impl.client.TrimmedClientTagApiImpl;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;

public interface TrimmedClientTagApi {
    static TrimmedClientTagApi getInstance() {
        return TrimmedClientTagApiImpl.INSTANCE;
    }

    <T> TagHolder<T> getTag(ClientTagKey<T> clientTagKey);

    <T> Codec<TagHolder<T>> tagCodec(KeyResolver<T> keyResolver);
}
