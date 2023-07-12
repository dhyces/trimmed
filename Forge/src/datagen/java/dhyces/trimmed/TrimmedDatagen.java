package dhyces.trimmed;

import dhyces.trimmed.model.VanillaItemModelOverrideProvider;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

// as much as I dislike EBS magic annotations, it is the only way I see of actually separating datagen from main src
@Mod.EventBusSubscriber(modid = Trimmed.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class TrimmedDatagen {

    @SubscribeEvent
    static void setupDatagen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();

        generator.addProvider(event.includeClient(), new VanillaItemModelOverrideProvider(packOutput));
    }
}