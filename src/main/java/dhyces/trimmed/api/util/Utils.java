package dhyces.trimmed.api.util;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class Utils {
    public static boolean endsInJson(ResourceLocation resourceLocation) {
        return resourceLocation.getPath().endsWith(".json");
    }

    public static String sanitize(String toSanitize, String... toRemove) {
        for (String removal : toRemove) {
            toSanitize = toSanitize.replace(removal, "");
        }
        return toSanitize;
    }

    public static ResourceLocation sanitizedPath(ResourceLocation resourceLocation, String... toRemove) {
        return new ResourceLocation(resourceLocation.getNamespace(), sanitize(resourceLocation.getPath(), toRemove));
    }

    /**
     * Creates a directory path for registered objects. Vanilla does not have the namespace, however modded registries
     * should have a parent directory with the mod's namespace
     */
    public static <T> String prefix(ResourceKey<? extends Registry<T>> registryResourceKey) {
        ResourceLocation location = registryResourceKey.location();
        return location.getNamespace().equals("minecraft") ? location.getPath() : location.getNamespace() + "/" + location.getPath();
    }
}
