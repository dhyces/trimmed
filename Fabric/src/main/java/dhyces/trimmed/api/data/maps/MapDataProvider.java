package dhyces.trimmed.api.data.maps;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.data.CachedOutput;

import java.util.concurrent.CompletableFuture;

public class MapDataProvider extends BaseMapDataProvider {
    public MapDataProvider(FabricDataOutput packOutput, String modid, String prefix) {
        super(packOutput, modid, prefix);
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }
}
