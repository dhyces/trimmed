package dev.dhyces.trimmed.modhelper.services.helpers;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.JsonOps;
import net.fabricmc.api.EnvType;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceCondition;
import net.fabricmc.fabric.api.resource.conditions.v1.ResourceConditions;
import net.fabricmc.fabric.impl.resource.conditions.ResourceConditionsImpl;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

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
    public <T> Optional<T> decodeWithConditions(Codec<T> codec, DynamicOps<JsonElement> jsonOps, JsonObject jsonObject) {
        if (jsonObject.has(ResourceConditions.CONDITIONS_KEY)) {
            DataResult<ResourceCondition> conditions = ResourceCondition.CONDITION_CODEC.parse(jsonOps, jsonObject.get(ResourceConditions.CONDITIONS_KEY));

            if (conditions.isSuccess() && !conditions.getOrThrow().test(null)) {
                return Optional.empty();
            }
        }
        return Optional.of(codec.parse(JsonOps.INSTANCE, jsonObject).getOrThrow(JsonParseException::new));
    }

    @Override
    public <T> T getRegistryValue(@Nullable RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey, ResourceLocation valueKey) {
        if (registryAccess != null) {
            return registryAccess.lookup(registryKey).map(reg -> reg.getValue(valueKey)).orElse(null);
        }
        return (T)BuiltInRegistries.REGISTRY.getValue(registryKey.location()).getValue(valueKey);
    }
}
