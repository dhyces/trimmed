package dev.dhyces.trimmed.impl.client;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import dev.dhyces.trimmed.api.client.tag.TagHolder;
import dev.dhyces.trimmed.api.client.TrimmedClientTagApi;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;

public final class TrimmedClientTagApiImpl implements TrimmedClientTagApi {
    public static final TrimmedClientTagApi INSTANCE = new TrimmedClientTagApiImpl();

    @Override
    public <T> TagHolder<T> getTag(ClientTagKey<T> clientTagKey) {
        return ClientTagManager.getHolder(clientTagKey);
    }

    @Override
    public <T> Codec<TagHolder<T>> tagCodec(KeyResolver<T> keyResolver) {
        return ClientTagKey.codec(keyResolver).flatComapMap(ClientTagManager::getHolder, tTagHolder ->
                tTagHolder.getKey().map(DataResult::success).orElseGet(() -> DataResult.error(() -> "No key is present for tag holder"))
        );
    }
}
