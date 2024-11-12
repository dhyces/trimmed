package dev.dhyces.trimmed.impl.mixin.client.api;

import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.impl.client.TrimmedClientMapApiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TrimmedClientMapApi.class)
public interface TrimmedClientMapApiMixin {
    /**
     * @author dhyces
     * @reason impl api
     */
    @Overwrite(remap = false)
    static TrimmedClientMapApi getInstance() {
        return TrimmedClientMapApiImpl.INSTANCE;
    }
}
