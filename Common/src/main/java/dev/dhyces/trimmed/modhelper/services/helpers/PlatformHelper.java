package dev.dhyces.trimmed.modhelper.services.helpers;

import com.google.gson.JsonObject;
import com.mojang.serialization.Codec;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public interface PlatformHelper {
    boolean isModLoaded(String modid);
    boolean isClientDist();

    boolean isProduction();


    String resolveRegistryPath(ResourceKey<? extends Registry<?>> resourceKey);

    <T> boolean modRegistryExists(ResourceKey<? extends Registry<T>> mod);

    <T> Optional<T> decodeWithConditions(Codec<T> codec, JsonObject jsonObject);


    <T> T getRegistryValue(@Nullable RegistryAccess registryAccess, ResourceKey<? extends Registry<T>> registry, ResourceLocation valueKey);
}
