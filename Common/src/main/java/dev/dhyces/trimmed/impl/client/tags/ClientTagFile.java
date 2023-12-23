package dev.dhyces.trimmed.impl.client.tags;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.tags.TagEntry;

import java.util.Set;

public record ClientTagFile(Set<TagEntry> tags, boolean isReplace) {
    public static final Codec<ClientTagFile> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CodecUtil.setOf(TagEntry.CODEC).fieldOf("values").forGetter(ClientTagFile::tags),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(ClientTagFile::isReplace)
            ).apply(instance, ClientTagFile::new)
    );
}
