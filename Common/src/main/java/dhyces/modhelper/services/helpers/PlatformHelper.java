package dhyces.modhelper.services.helpers;

import com.google.gson.JsonObject;
import dhyces.modhelper.services.util.Platform;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface PlatformHelper {
    boolean isModLoaded(String modid);
    boolean isClientDist();

    boolean isProduction();

    Platform getPlatform();

    String resolveRegistryPath(ResourceKey<? extends Registry<?>> resourceKey);

    <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> mod);

    boolean shouldPassConditions(JsonObject jsonObject);

    boolean isLoadingStateValid();

    <T> T getRegistryValue(ResourceKey<? extends Registry<T>> registry, ResourceLocation valueKey);
}
