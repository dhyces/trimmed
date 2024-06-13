package dev.dhyces.trimmed.impl.client;

import net.minecraft.core.RegistryAccess;

public record GameRegistryHolder(RegistryAccess registryAccess, boolean isSynced) {
}