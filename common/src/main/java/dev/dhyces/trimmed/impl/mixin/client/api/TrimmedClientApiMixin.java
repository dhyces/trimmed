package dev.dhyces.trimmed.impl.mixin.client.api;

import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.impl.client.TrimmedClientApiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TrimmedClientApi.class)
public interface TrimmedClientApiMixin {
    /**
     * @author dhyces
     * @reason impl api
     */
    @Overwrite(remap = false)
    static TrimmedClientApi getInstance() {
        return TrimmedClientApiImpl.INSTANCE;
    }
}
