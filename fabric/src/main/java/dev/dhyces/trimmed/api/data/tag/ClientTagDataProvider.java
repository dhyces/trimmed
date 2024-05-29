package dev.dhyces.trimmed.api.data.tag;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.data.client.tag.appenders.ClientTagAppender;
import dev.dhyces.trimmed.impl.client.tags.ClientTagKey;
import dev.dhyces.trimmed.impl.client.tags.manager.ClientTagManager;
import net.fabricmc.fabric.impl.datagen.FabricTagBuilder;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.tags.TagFile;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class ClientTagDataProvider extends BaseClientTagDataProvider {


    public ClientTagDataProvider(PackOutput packOutput, String modid) {
        super(packOutput, modid, ClientTagManager.PATH);
    }

    protected abstract void addTags();

    public ClientTagAppender clientTag(ClientTagKey clientTagKey) {
        return new ClientTagAppender(modid, getOrCreateBuilder(clientTagKey.getTagId()));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.addTags();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            DataResult<JsonElement> jsonResult = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(entry.getValue().build(), ((FabricTagBuilder)entry.getValue()).fabric_isReplaced()));
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
