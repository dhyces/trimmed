package dev.dhyces.trimmed.api.data.map;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.data.client.map.appenders.ClientMapAppender;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.util.Utils;
import dev.dhyces.trimmed.impl.client.maps.KeyResolvers;
import dev.dhyces.trimmed.impl.client.maps.manager.ClientMapManager;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.server.packs.PackType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;

public abstract class ClientMapDataProvider<K> extends NeoBaseMapDataProvider<K, KeyResolver<K>> {
    public ClientMapDataProvider(PackOutput packOutput, String modid, KeyResolver<K> keyResolver, ExistingFileHelper existingFileHelper) {
        super(packOutput, modid, keyResolver, new ExistingFileHelper.ResourceType(PackType.CLIENT_RESOURCES, ".json", ClientMapManager.PATH + Utils.namespacedPath(KeyResolvers.getId(keyResolver))), existingFileHelper);
    }

    public <V> ClientMapAppender<K, V> map(MapKey<K, V> mapKey) {
        return new ClientMapAppender<>(getOrCreateBuilder(mapKey));
    }

    protected abstract void addMaps();

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.addMaps();
        complete();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            var codec = MapFile.codec(entry.getKey().getType().getKeyResolver().getCodec(), entry.getKey().getType().getValueCodec());

            DataResult<JsonElement> elementResult = codec.encodeStart(JsonOps.INSTANCE, Utils.unsafeCast(entry.getValue().build()));
            Path path = pathProvider.json(entry.getKey().getMapId());
            return DataProvider.saveStable(pOutput, elementResult.getOrThrow(), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ClientMapDataProvider for " + modid;
    }
}
