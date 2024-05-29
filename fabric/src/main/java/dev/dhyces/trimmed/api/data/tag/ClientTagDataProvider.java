package dev.dhyces.trimmed.api.data.tag;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.data.client.tag.appenders.ClientTagAppender;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class ClientTagDataProvider<T> extends FabricBaseClientTagDataProvider<T, KeyResolver<T>> {
    public ClientTagDataProvider(PackOutput packOutput, String modid, KeyResolver<T> keyResolver) {
        super(packOutput, modid, ClientTagManager.PATH, keyResolver);
    }

    protected abstract void addTags();

    public ClientTagAppender<T> clientTag(ClientTagKey<T> clientTagKey) {
        return new ClientTagAppender<>(getOrCreateBuilder(clientTagKey));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        // TODO: Still need to add future dependency as well as tag verification
        this.addTags();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            DataResult<JsonElement> jsonResult = ClientTagFile.codec(keyResolver).encodeStart(JsonOps.INSTANCE, entry.getValue().build());
            JsonElement json = jsonResult.getOrThrow();
            Path filePath = pathProvider.json(entry.getKey());
            return DataProvider.saveStable(pOutput, json, filePath);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ClientTagDataProvider for " + modid;
    }
}
