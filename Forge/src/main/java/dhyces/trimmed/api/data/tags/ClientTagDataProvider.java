package dhyces.trimmed.api.data.tags;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.tags.appenders.ClientTagAppender;
import dhyces.trimmed.impl.client.tags.ClientTagKey;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.minecraft.tags.TagFile;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class ClientTagDataProvider extends BaseClientTagDataProvider {

    protected static final ExistingFileHelper.IResourceType UNCHECKED_RESOURCE_TYPE = new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", "tags/unchecked");

    public ClientTagDataProvider(PackOutput packOutput, String modid, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, UNCHECKED_RESOURCE_TYPE, existingFileHelper);
    }

    protected abstract void addTags();

    public ClientTagAppender clientTag(ClientTagKey clientTagKey) {
        return new ClientTagAppender(getOrCreateBuilder(clientTagKey.getTagId()));
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.addTags();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            DataResult<JsonElement> jsonResult = TagFile.CODEC.encodeStart(JsonOps.INSTANCE, new TagFile(entry.getValue().build(), entry.getValue().isReplace()));
            JsonElement json = jsonResult.getOrThrow(false, Trimmed.LOGGER::error);
            Path filePath = pathProvider.json(entry.getKey());
            return DataProvider.saveStable(pOutput, json, filePath);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ClientTagDataProvider for " + modid;
    }
}
