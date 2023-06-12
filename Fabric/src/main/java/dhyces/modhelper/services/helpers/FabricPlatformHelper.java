package dhyces.modhelper.services.helpers;

import com.google.gson.JsonObject;
import dhyces.modhelper.services.util.Platform;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

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
    public Platform getPlatform() {
        return Platform.FABRIC;
    }

    @Override
    public String resolveRegistryPath(ResourceKey<? extends Registry<?>> resourceKey) {
        return resourceKey.location().getPath();
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
    public boolean isLoadingStateValid() {
        return true;
    }

    @Override
    public <T> T getRegistryValue(@Nullable RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey, ResourceLocation valueKey) {
        if (registryAccess != null) {
            return registryAccess.registry(registryKey).map(reg -> reg.get(valueKey)).orElse(null);
        }
        return (T)BuiltInRegistries.REGISTRY.get(registryKey.location()).get(valueKey);
    }
}
