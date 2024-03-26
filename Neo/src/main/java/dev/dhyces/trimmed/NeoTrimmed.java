package dev.dhyces.trimmed;

import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;

import java.util.function.Supplier;

@Mod(Trimmed.MODID)
public class NeoTrimmed {
    public NeoTrimmed(IEventBus modBus, Dist dist) {
        Trimmed.init();

        if (dist.isClient()) {
            NeoTrimmedClient.init(NeoForge.EVENT_BUS, modBus);
        }
    }
}
