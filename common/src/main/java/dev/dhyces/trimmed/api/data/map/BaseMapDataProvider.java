package dev.dhyces.trimmed.api.data.map;

import dev.dhyces.trimmed.api.KeyResolver;
import dev.dhyces.trimmed.api.maps.MapKey;
import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;

import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.function.Function;

public abstract class BaseMapDataProvider<K, R extends KeyResolver<K>> implements DataProvider {
    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final Map<MapKey<K, ?>, MapBuilder<K, ?>> builders;
    protected final R keyResolver;
    protected final CompletableFuture<MapLookup<K>> futureLookup;

    public BaseMapDataProvider(PackOutput packOutput, PackOutput.Target target, String modid, String prefix, R keyResolver) {
        this.packOutput = packOutput;
        this.pathProvider = packOutput.createPathProvider(target, prefix);
        this.modid = modid;
        this.builders = new Reference2ObjectOpenHashMap<>();
        this.keyResolver = keyResolver;
        this.futureLookup = new CompletableFuture<>();
    }

    public CompletableFuture<MapLookup<K>> contentsGetter() {
        return futureLookup;
    }

    protected void complete() {
        futureLookup.complete(builders::get);
    }

    @SuppressWarnings("unchecked")
    protected <V> MapBuilder<K, V> getOrCreateBuilder(MapKey<K, V> mapKey) {
        return (MapBuilder<K, V>) this.builders.computeIfAbsent(mapKey, resourceLocation -> {
            onBuilderCreation(mapKey);
            return new MapBuilder<>();
        });
    }

    protected <V> void onBuilderCreation(MapKey<K, V> mapKey) {}

    public interface MapLookup<K> extends Function<MapKey<K, ?>, MapBuilder<K, ?>> {
        default boolean containsKey(MapKey<K, ?> mapKey) {
            return apply(mapKey) != null;
        }
    }
}
