package dhyces.trimmed;

import dhyces.trimmed.model.VanillaItemModelOverrideProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class TrimmedDatagen {

    @SubscribeEvent
    void setupDatagen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new VanillaItemModelOverrideProvider(packOutput));
    }
}
