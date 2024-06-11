package dev.dhyces.trimmed.api.data.map;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.client.TrimmedClientApi;
import dev.dhyces.trimmed.api.data.map.appenders.MapAppender;
import dev.dhyces.trimmed.api.data.map.appenders.MappedMapAppender;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.util.Utils;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientMapDataProvider<K> extends NeoClientMapDataProvider<K> {
    public ClientMapDataProvider(PackOutput packOutput, String modid, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, existingFileHelper);
    }

    public <V> MapAppender<K, V> map(MapKey<K, V> mapKey) {
        return new MapAppender<>(getOrCreateBuilder(mapKey));
    }

    public <V> MapAppender<K, V> map(MapKey<K, V> mapKey, Function<K, @Nullable ResourceLocation> encoder) {
        return new MappedMapAppender<>(getOrCreateBuilder(mapKey), encoder);
    }

    protected abstract void addMaps();

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.addMaps();
        complete();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            var codec = MapFile.codec(entry.getKey().getType().getValueCodec());

            DataResult<JsonElement> elementResult = codec.encodeStart(JsonOps.INSTANCE, Utils.unsafeCast(entry.getValue().build()));
            Path path = pathProvider.json(entry.getKey().compilePathAndIdNamespace().withPrefix(Utils.namespacedPath(TrimmedClientApi.getInstance().getId(entry.getKey().getType().getKeyResolver())) + '/'));
            return DataProvider.saveStable(pOutput, elementResult.getOrThrow(), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ClientMapDataProvider for " + modid;
    }
}
