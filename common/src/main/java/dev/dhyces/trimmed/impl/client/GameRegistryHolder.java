package dev.dhyces.trimmed.impl.client;

import net.minecraft.core.HolderLookup;

public record GameRegistryHolder(HolderLookup.Provider lookupProvider, boolean isSynced) {
}