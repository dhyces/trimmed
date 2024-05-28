package dev.dhyces.trimmed.api.data.models.override;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;

import java.util.Set;

public record ItemOverrideFile(Set<ItemOverrideProvider> overrideProviders, boolean replace) {
    public static final Codec<ItemOverrideFile> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ItemOverrideProvider.SET_MAP_CODEC.forGetter(ItemOverrideFile::overrideProviders),
                    Codec.BOOL.optionalFieldOf("replace", false).forGetter(ItemOverrideFile::replace)
            ).apply(instance, ItemOverrideFile::new)
    );
}
