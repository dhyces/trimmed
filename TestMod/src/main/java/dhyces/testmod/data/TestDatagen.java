package dhyces.testmod.data;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import dhyces.testmod.data.trimmed.*;
import dhyces.testmod.registry.CustomObj;
import dhyces.testmod.registry.CustomRegistration;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Stream;

public class TestDatagen {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap);

    public static void init(IEventBus modBus) {
        modBus.addListener(TestDatagen::gatherDataEvent);
    }

    private static void gatherDataEvent(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, BUILDER, Set.of("testmod")));
        generator.addProvider(event.includeServer(), new TestItemTagProvider(packOutput, lookupProvider, "testmod", event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new TestRecipeProvider(packOutput));

//        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestAtlasProvider>) TestAtlasProvider::new);
        generator.addProvider(event.includeClient(), new TestLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new TestModelProvider(packOutput, "testmod", new ExistingFileHelper(Collections.emptyList(), Collections.emptySet(), false, null, null)));

        generator.addProvider(event.includeClient(), new TestItemOverrideProvider(packOutput));
        generator.addProvider(event.includeClient(), new TestClientTagProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientItemTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientCustomObjTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}
