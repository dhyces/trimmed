package dhyces.modhelper.services.helpers;

import com.google.gson.JsonObject;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public interface PlatformHelper {
    boolean isModLoaded(String modid);
    boolean isClientDist();

    boolean isProduction();

    <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> mod);

    boolean shouldPassConditions(JsonObject jsonObject);

    <T> T getRegistryValue(ResourceKey<? extends Registry<T>> registry, ResourceLocation valueKey);
}
