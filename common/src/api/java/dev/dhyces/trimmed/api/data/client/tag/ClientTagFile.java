package dev.dhyces.trimmed.api.data.client.tag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.util.CodecUtil;

import java.util.Set;

public record ClientTagFile(Set<ClientTagEntry> entries, boolean replace) {
    public static final Codec<ClientTagFile> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CodecUtil.setOf(ClientTagEntry.CODEC).fieldOf("values").forGetter(ClientTagFile::entries),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(ClientTagFile::replace)
            ).apply(instance, ClientTagFile::new)
    );
}
