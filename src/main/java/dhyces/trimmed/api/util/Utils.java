package dhyces.trimmed.api.util;

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
}
