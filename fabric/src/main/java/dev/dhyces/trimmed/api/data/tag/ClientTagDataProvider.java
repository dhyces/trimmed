package dev.dhyces.trimmed.api.data.tag;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.data.client.tag.BaseClientTagDataProvider;
import dev.dhyces.trimmed.api.data.client.tag.ClientTagFile;
import dev.dhyces.trimmed.api.data.client.tag.appenders.ClientTagAppender;
import dev.dhyces.trimmed.api.client.tag.ClientTagKey;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientTagDataProvider<T> extends BaseClientTagDataProvider<T, KeyResolver<T>> {
    public ClientTagDataProvider(PackOutput packOutput, String modid, KeyResolver<T> keyResolver) {
        super(packOutput, modid, keyResolver);
    }

    protected abstract void addTags();

    public ClientTagAppender<T> tag(ClientTagKey<T> clientTagKey) {
        return new ClientTagAppender<>(getOrCreateBuilder(clientTagKey));
    }

    public ClientTagAppender.Mapped<T> tag(ClientTagKey<T> clientTagKey, Function<T, @Nullable ResourceLocation> encoder) {
        return new ClientTagAppender.Mapped<>(getOrCreateBuilder(clientTagKey), encoder);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        // TODO: Still need to add future dependency as well as tag verification
        this.addTags();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            DataResult<JsonElement> jsonResult = ClientTagFile.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue().build());
            JsonElement json = jsonResult.getOrThrow();
            Path filePath = pathProvider.json(entry.getKey());
            return DataProvider.saveStable(pOutput, json, filePath);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ClientTagDataProvider<%s> for %s".formatted(KeyResolvers.getId(keyResolver), modid);
    }
}
