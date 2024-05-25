package dev.dhyces.trimmed;

import net.fabricmc.api.ModInitializer;

@SuppressWarnings("unused")
public class FabricTrimmed implements ModInitializer {
    @Override
    public void onInitialize() {
        Trimmed.init();
    }
}
