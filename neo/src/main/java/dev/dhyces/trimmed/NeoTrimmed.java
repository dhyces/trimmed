package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.TrimmedReference;
import net.neoforged.fml.common.Mod;

@SuppressWarnings("unused")
@Mod(TrimmedReference.MODID)
public class NeoTrimmed {
    public NeoTrimmed() {
        Trimmed.init();
    }
}
