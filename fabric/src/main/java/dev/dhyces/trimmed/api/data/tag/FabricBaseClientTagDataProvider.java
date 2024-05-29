package dev.dhyces.trimmed.api.data.tag;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.data.client.tag.BaseClientTagDataProvider;
import net.minecraft.data.PackOutput;

public abstract class FabricBaseClientTagDataProvider<T, R extends KeyResolver<T>> extends BaseClientTagDataProvider<T, R> {
    public FabricBaseClientTagDataProvider(PackOutput packOutput, String modid, String prefix, R keyResolver) {
        super(packOutput, modid, prefix, keyResolver);
    }
}
