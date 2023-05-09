package dhyces.trimmed.impl.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public final class UnresolvedMap<R, T> implements Iterable<Map.Entry<ResourceKey<? extends Registry<R>>, Map<ResourceLocation, T>>> {

    private final Map<ResourceKey<? extends Registry<R>>, Map<ResourceLocation, T>> backing = new HashMap<>();

    public void add(ResourceKey<? extends Registry<R>> handlerKey, ResourceLocation tagId, T data) {
        backing.computeIfAbsent(handlerKey, rResourceKey -> new HashMap<>()).put(tagId, data);
    }

    @NotNull
    @Override
    public Iterator<Map.Entry<ResourceKey<? extends Registry<R>>, Map<ResourceLocation, T>>> iterator() {
        return backing.entrySet().iterator();
    }
}