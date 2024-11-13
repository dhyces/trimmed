package dev.dhyces.trimmed;

import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.model.VanillaItemModelOverrideProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.data.event.GatherDataEvent;

@Mod(TrimmedReference.MODID)
public class TrimmedDatagen {
    public TrimmedDatagen(IEventBus modBus) {
        modBus.addListener(this::setupDatagen);
    }

    private void setupDatagen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new VanillaItemModelOverrideProvider(packOutput));
    }
}