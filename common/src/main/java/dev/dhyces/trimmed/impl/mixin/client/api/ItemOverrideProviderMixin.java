package dev.dhyces.trimmed.impl.mixin.client.api;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(ItemOverrideProvider.class)
public interface ItemOverrideProviderMixin {
    /**
     * @author dhyces
     * @reason impl api
     */
    @Overwrite(remap = false)
    static Codec<MapCodec<? extends ItemOverrideProvider>> getRegistryCodec() {
        return ItemOverrideProviderRegistry.CODEC;
    }
}
