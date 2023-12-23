package dev.dhyces.trimmed.modhelper.services.helpers;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.Util;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.common.crafting.conditions.ICondition;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLLoader;
import net.minecraftforge.registries.RegistryManager;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public final class ForgePlatformHelper implements PlatformHelper {
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
        return RegistryManager.ACTIVE.getRegistry(modRegistry) != null;
    }

    @Override
    public <T> Optional<T> decodeWithConditions(Codec<T> codec, JsonObject jsonObject) {
        if (ForgeHooks.readAndTestCondition(ICondition.IContext.TAGS_INVALID, jsonObject)) {
            return Optional.empty();
        }
        return Optional.of(Util.getOrThrow(codec.parse(JsonOps.INSTANCE, jsonObject).promotePartial(s -> {}), JsonParseException::new));
    }

    @Override
    public <T> T getRegistryValue(@Nullable RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registryKey, ResourceLocation valueKey) {
        if (registryAccess != null) {
            return registryAccess.registry(registryKey).map(registry -> registry.get(valueKey)).orElse(null);
        }
        return RegistryManager.ACTIVE.getRegistry(registryKey).getValue(valueKey);
    }
}
