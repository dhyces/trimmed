package dhyces.testmod.data;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import dhyces.testmod.data.trimmed.TestClientItemTagProvider;
import dhyces.testmod.data.trimmed.TestClientTagProvider;
import dhyces.testmod.data.trimmed.TestItemOverrideProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TestDatagen {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap);

    public static void init(IEventBus modBus) {
        modBus.addListener(TestDatagen::gatherDataEvent);
    }

    private static void gatherDataEvent(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), (DataProvider.Factory<DatapackBuiltinEntriesProvider>) packOutput -> new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, BUILDER, Set.of("testmod")));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<TestItemTagProvider>) packOutput -> new TestItemTagProvider(packOutput, lookupProvider, "testmod", event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<TestRecipeProvider>) TestRecipeProvider::new);

        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestAtlasProvider>) TestAtlasProvider::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestLangProvider>) TestLangProvider::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestModelProvider>) packOutput -> new TestModelProvider(packOutput, "testmod", new ExistingFileHelper(Collections.emptyList(), Collections.EMPTY_SET, false, null, null)));

        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestItemOverrideProvider>) TestItemOverrideProvider::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestClientTagProvider>) packOutput -> new TestClientTagProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestClientItemTagProvider>) packOutput -> new TestClientItemTagProvider(packOutput, event.getLookupProvider(), event.getExistingFileHelper()));
    }
}
