package dhyces.modhelper.services.helpers;

import com.google.gson.JsonObject;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

public final class FabricPlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modid) {
        return FabricLoader.getInstance().isModLoaded(modid);
    }

    @Override
    public boolean isClientDist() {
        return FabricLoader.getInstance().getEnvironmentType().equals(EnvType.CLIENT);
    }

    @Override
    public boolean isProduction() {
        return !FabricLoader.getInstance().isDevelopmentEnvironment();
    }

    @Override
    public <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> modRegistry) {
        try {
            return BuiltInRegistries.REGISTRY.registryKeySet().contains(modRegistry);
        } catch (Exception e) {
            return false;
        }
    }

    @Override
    public boolean shouldPassConditions(JsonObject jsonObject) {
        return ResourceConditions.objectMatchesConditions(jsonObject);
    }

    @Override
    public <T> T getRegistryValue(ResourceKey<? extends Registry<T>> registry, ResourceLocation valueKey) {
        return (T)BuiltInRegistries.REGISTRY.get(registry.location()).get(valueKey);
    }
}
