package dev.dhyces.trimmed;

import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(Trimmed.MODID)
public class NeoTrimmed {
    public NeoTrimmed(IEventBus modBus, ModContainer container) {
        Trimmed.init();
    }
}
