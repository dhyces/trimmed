package dev.dhyces.trimmed.api.data.client.tag;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.Map;

public abstract class BaseClientTagDataProvider<T, R extends KeyResolver<T>> implements DataProvider {
    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final Map<ResourceLocation, ClientTagBuilder<T>> builders = new Object2ObjectLinkedOpenHashMap<>();
    protected final R keyResolver;

    public BaseClientTagDataProvider(PackOutput packOutput, String modid, R keyResolver) {
        this.packOutput = packOutput;
        this.modid = modid;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, ClientTagManager.PATH + '/' + Utils.namespacedPath(TrimmedClientApi.getInstance().getId(keyResolver)));
        this.keyResolver = keyResolver;
    }

    protected ClientTagBuilder<T> getOrCreateBuilder(ClientTagKey<T> clientTagKey) {
        return this.builders.computeIfAbsent(clientTagKey.getTagId(), resourceLocation -> {
            onBuilderCreation(resourceLocation);
            return new ClientTagBuilder<>();
        });
    }

    protected void onBuilderCreation(ResourceLocation id) {}

    protected boolean doesTagExist(ResourceLocation clientTagId) {
        return builders.get(clientTagId) != null;
    }
}
