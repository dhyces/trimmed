package dhyces.trimmed.impl.resources;

import dhyces.trimmed.api.util.Utils;
import dhyces.trimmed.impl.util.RegistryType;
import dhyces.trimmed.modhelper.services.Services;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceKey;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public record RegistryPathInfo(ResourceKey<? extends Registry<?>> resourceKey, RegistryType registryType, boolean isModded) implements PathInfo {

    public static RegistryPathInfo implied(ResourceKey<? extends Registry<?>> resourceKey, RegistryType registryType) {
        return new RegistryPathInfo(resourceKey, registryType, !resourceKey.location().getNamespace().equals("minecraft"));
    }

    public static Collection<RegistryPathInfo> gatherRegistryInfos(@Nullable RegistryAccess registryAccess) {
        if (registryAccess != null) {
            return registryAccess.registries()
                    .map(RegistryAccess.RegistryEntry::key)
                    .map(resourceKey -> {
                        RegistryType registryType = BuiltInRegistries.REGISTRY.containsKey(resourceKey.location()) ? RegistryType.STATIC : RegistryType.DATAPACK;
                        return RegistryPathInfo.implied(resourceKey, registryType);
                    })
                    .toList();
        } else {
            return BuiltInRegistries.REGISTRY.registryKeySet().stream()
                    .map(resourceKey -> RegistryPathInfo.implied(resourceKey, RegistryType.STATIC))
                    .toList();
        }
    }

    public <T> ResourceKey<? extends Registry<T>> castRegistryKey() {
        return Utils.unsafeCast(resourceKey);
    }

    @Override
    public String getPath() {
        return Services.PLATFORM_HELPER.resolveRegistryPath(resourceKey);
    }
}