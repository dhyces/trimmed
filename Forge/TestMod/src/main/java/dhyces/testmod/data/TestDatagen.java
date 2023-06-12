package dhyces.testmod.data;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import dhyces.testmod.TrimmedTest;
import dhyces.testmod.data.trimmed.*;
import dhyces.testmod.data.trimmed.registrymaps.ClientBlockProvider;
import dhyces.testmod.data.trimmed.registrymaps.ClientDamageTypeProvider;
import dhyces.testmod.data.trimmed.registrymaps.EntityToEntityProvider;
import dhyces.testmod.data.trimmed.registrytags.TestClientCustomObjTagProvider;
import dhyces.testmod.data.trimmed.registrytags.TestClientItemTagProvider;
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
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, BUILDER, Set.of(TrimmedTest.MODID)));
        generator.addProvider(event.includeServer(), new TestItemTagProvider(packOutput, lookupProvider, TrimmedTest.MODID, event.getExistingFileHelper()));
        generator.addProvider(event.includeServer(), new TestRecipeProvider(packOutput));

        generator.addProvider(event.includeClient(), new TestLangProvider(packOutput));
        generator.addProvider(event.includeClient(), new TestModelProvider(packOutput, TrimmedTest.MODID, event.getExistingFileHelper()));

        generator.addProvider(event.includeClient(), new TestItemOverrideProvider(packOutput));
        generator.addProvider(event.includeClient(), new TestClientTagProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientItemTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientCustomObjTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientMapProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ClientBlockProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ClientDamageTypeProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new EntityToEntityProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}
