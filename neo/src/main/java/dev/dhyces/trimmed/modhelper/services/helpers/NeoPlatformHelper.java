package dev.dhyces.trimmed.modhelper.services.helpers;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.conditions.ICondition;
import net.neoforged.neoforge.common.util.NeoForgeExtraCodecs;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class NeoPlatformHelper implements PlatformHelper {
    @Override
    public boolean isModLoaded(String modid) {
        return ModList.get().isLoaded(modid);
    }

    @Override
    public boolean isClientDist() {
        return FMLLoader.getDist().isClient();
    }

    @Override
    public boolean isProduction() {
        return FMLLoader.isProduction();
    }

    @Override
    public String resolveRegistryPath(ResourceKey<? extends Registry<?>> resourceKey) {
        String registryNamespace = resourceKey.location().getNamespace();
        if (registryNamespace.equals("minecraft")) {
            return resourceKey.location().getPath();
        } else {
            return registryNamespace + "/" + resourceKey.location().getPath();
        }
    }

    @Override
    public <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> modRegistry) {
        return BuiltInRegistries.REGISTRY.containsKey(modRegistry.location());
    }

    @Override
    public <T> Optional<T> decodeWithConditions(Codec<T> codec, JsonObject jsonObject) {
        return ICondition.getConditionally(codec, JsonOps.INSTANCE, jsonObject);
    }

    @Override
    public <T> T getRegistryValue(@Nullable RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey, ResourceLocation valueKey) {
        if (registryAccess != null) {
            return registryAccess.registry(registryKey).map(registry -> registry.get(valueKey)).orElse(null);
        }
        return (T) BuiltInRegistries.REGISTRY.get(registryKey.location()).get(valueKey);
    }
}
