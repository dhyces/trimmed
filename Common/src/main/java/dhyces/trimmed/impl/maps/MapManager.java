package dhyces.trimmed.impl.maps;

import dhyces.modhelper.services.Services;
import net.minecraft.core.RegistryAccess;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

public class MapManager implements PreparableReloadListener {

    private RegistryAccess registryAccess;

    public MapManager(RegistryAccess.Frozen registryAccess) {
        this.registryAccess = registryAccess;
    }

    @Override
    public CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller preparationsProfiler, ProfilerFiller reloadProfiler, Executor backgroundExecutor, Executor gameExecutor) {
        if (Services.PLATFORM_HELPER.isLoadingStateValid()) {
            return CompletableFuture.completedFuture(null).thenCompose(preparationBarrier::wait).thenAccept(o -> {});
        } else {
            return CompletableFuture.completedFuture(null);
        }
    }
}
