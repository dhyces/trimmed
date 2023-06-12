package dhyces.trimmed.impl.resources;

import com.google.common.collect.ImmutableSet;
import net.minecraft.core.RegistryAccess;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public sealed interface PathInfo permits UncheckedPathInfo, RegistryPathInfo {
    String getPath();

    static Collection<PathInfo> gatherAllInfos(@Nullable RegistryAccess registryAccess) {
        ImmutableSet.Builder<PathInfo> builder = ImmutableSet.builder();
        builder.add(UncheckedPathInfo.INSTANCE);
        builder.addAll(RegistryPathInfo.gatherRegistryInfos(registryAccess));
        return builder.build();
    }
}