package dhyces.trimmed.api.data.maps;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.data.maps.appenders.ClientMapAppender;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class ClientMapDataProvider extends BaseMapDataProvider {

    public ClientMapDataProvider(FabricDataOutput packOutput, String modid) {
        super(packOutput, modid, "maps/unchecked");
    }

    public ClientMapAppender<String> map(ClientMapKey clientMapKey) {
        return new ClientMapAppender<>(getOrCreateBuilder(clientMapKey.getMapId()), Function.identity());
    }

    public <V> ClientMapAppender<V> mapWithMapper(ClientMapKey clientMapKey, Class<V> valueClass, Function<V, String> mapper) {
        return new ClientMapAppender<>(getOrCreateBuilder(clientMapKey.getMapId()), mapper);
    }

    protected abstract void addMaps();

    @Override
    public CompletableFuture<?> run(CachedOutput pOutput) {
        this.addMaps();
        return CompletableFuture.allOf(builders.entrySet().stream().map(entry -> {
            DataResult<JsonElement> elementResult = MapFile.CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue().build());
            Path path = pathProvider.json(entry.getKey());
            return DataProvider.saveStable(pOutput, elementResult.getOrThrow(false, Trimmed.LOGGER::error), path);
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ClientMapDataProvider for " + modid;
    }
}
