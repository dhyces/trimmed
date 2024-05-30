package dev.dhyces.trimmed.api.data.client.tag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.util.CodecUtil;

import java.util.Set;

public record ClientTagFile<T>(Set<ClientTagEntry<T>> entries, boolean replace) {
    public static <T> Codec<ClientTagFile<T>> codec(KeyResolver<T> keyResolver) {
        return RecordCodecBuilder.create(instance ->
                instance.group(
                        CodecUtil.setOf(ClientTagEntry.codec(keyResolver)).fieldOf("values").forGetter(ClientTagFile::entries),
                        Codec.BOOL.optionalFieldOf("replace", false).forGetter(ClientTagFile::replace)
                ).apply(instance, ClientTagFile::new)
        );
    }
}
