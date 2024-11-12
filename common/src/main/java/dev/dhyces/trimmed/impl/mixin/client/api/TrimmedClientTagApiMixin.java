package dev.dhyces.trimmed.impl.mixin.client.api;

import dev.dhyces.trimmed.api.client.TrimmedClientTagApi;
import dev.dhyces.trimmed.impl.client.TrimmedClientTagApiImpl;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(TrimmedClientTagApi.class)
public interface TrimmedClientTagApiMixin {
    /**
     * @author dhyces
     * @reason impl api
     */
    @Overwrite(remap = false)
    static TrimmedClientTagApi getInstance() {
        return TrimmedClientTagApiImpl.INSTANCE;
    }
}
