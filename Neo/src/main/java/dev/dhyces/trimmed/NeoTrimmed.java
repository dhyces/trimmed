package dev.dhyces.trimmed;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Trimmed.MODID)
public class NeoTrimmed {
    public NeoTrimmed(IEventBus modBus) {
        Trimmed.init();

        if (FMLLoader.getDist().isClient()) {
            NeoTrimmedClient.init(NeoForge.EVENT_BUS, modBus);
        }
    }
}
