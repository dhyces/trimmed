package dhyces.testmod.data;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collections;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class TrimmedDatagen {

    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap)
            .add(Registries.TRIM_PATTERN, ModTrimPatterns::bootstrap);

    public static void init(IEventBus modBus) {
        modBus.addListener(TrimmedDatagen::gatherDataEvent);
    }

    private static void gatherDataEvent(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        generator.addProvider(event.includeServer(), (DataProvider.Factory<DatapackBuiltinEntriesProvider>) packOutput -> new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, BUILDER, Set.of("testmod")));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<TrimmedItemTagProvider>) packOutput -> new TrimmedItemTagProvider(packOutput, lookupProvider, "testmod", event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), (DataProvider.Factory<TestRecipeProvider>) TestRecipeProvider::new);

        generator.addProvider(event.includeClient(), (DataProvider.Factory<TrimmedAtlasProvider>) TrimmedAtlasProvider::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TrimmedLangProvider>) TrimmedLangProvider::new);
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TrimmedModelProvider>) packOutput -> new TrimmedModelProvider(packOutput, "testmod", new ExistingFileHelper(Collections.emptyList(), Collections.EMPTY_SET, false, null, null)));
        generator.addProvider(event.includeClient(), (DataProvider.Factory<TestItemOverrideProvider>) TestItemOverrideProvider::new);
    }
}
