package dev.dhyces.trimmed;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

@Mod(Trimmed.MODID)
public class NeoTrimmed {
    public NeoTrimmed(IEventBus modBus, ModContainer container, Dist dist) {
        Trimmed.init();

        if (dist.isClient()) {
            NeoTrimmedClient.init(NeoForge.EVENT_BUS, modBus);
        }
    }
}
