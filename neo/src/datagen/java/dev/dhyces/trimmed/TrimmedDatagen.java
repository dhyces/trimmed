package dev.dhyces.trimmed;

import dev.dhyces.trimmed.model.VanillaItemModelOverrideProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

// as much as I dislike EBS magic annotations, it is the only way I see of actually separating datagen from main src
@EventBusSubscriber(modid = Trimmed.MODID, bus = EventBusSubscriber.Bus.MOD)
public class TrimmedDatagen {

    @SubscribeEvent
    static void setupDatagen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new VanillaItemModelOverrideProvider(packOutput));
    }
}