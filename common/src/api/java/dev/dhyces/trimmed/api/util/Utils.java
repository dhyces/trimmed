package dev.dhyces.trimmed.api.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class Utils {
    /**
     * Creates a directory path for registered objects. Vanilla does not have the namespace, however modded registries
     * should have a parent directory with the mod's namespace
     */
    public static <T> String namespacedLocation(ResourceKey<? extends Registry<T>> registryResourceKey) {
        return delimitIfDefault(registryResourceKey.location(), '/');
    }

    public static String namespacedPath(ResourceLocation location) {
        return delimitIfDefault(location, '/');
    }

    public static String delimitIfDefault(ResourceLocation location, char delimiter) {
        return location.getNamespace().equals("minecraft") ? location.getPath() : location.getNamespace() + delimiter + location.getPath();
    }

    @SuppressWarnings("unchecked")
    public static <T> T unsafeCast(Object o) {
        return (T) o;
    }
}
