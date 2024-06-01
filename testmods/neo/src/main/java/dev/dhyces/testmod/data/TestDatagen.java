package dev.dhyces.testmod.data;

import dev.dhyces.testmod.data.trimmed.TestModelSourceProvider;
import dev.dhyces.testmod.registry.ModArmorMaterials;
import dev.dhyces.testmod.registry.ModItems;
import dev.dhyces.testmod.ModTrimMaterials;
import dev.dhyces.testmod.ModTrimPatterns;
import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.data.trimmed.TestClientMapProvider;
import dev.dhyces.testmod.data.trimmed.TestClientTagProvider;
import dev.dhyces.testmod.data.trimmed.TestItemOverrideProvider;
import dev.dhyces.testmod.data.trimmed.registrymaps.ClientBlockProvider;
import dev.dhyces.testmod.data.trimmed.registrymaps.ClientDamageTypeProvider;
import dev.dhyces.testmod.data.trimmed.registrymaps.EntityToEntityProvider;
import dev.dhyces.testmod.data.trimmed.registrytags.TestClientCustomObjTagProvider;
import dev.dhyces.testmod.data.trimmed.registrytags.TestClientItemTagProvider;
import dev.dhyces.trimmed.api.data.TrimDatagenSuite;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.data.LanguageProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.neoforged.neoforge.data.loading.DatagenModLoader;

import java.util.concurrent.CompletableFuture;

@Mod(TrimmedTest.MODID)
public class TestDatagen {

    public TestDatagen(IEventBus modBus) {
        if (DatagenModLoader.isRunningDataGen()) {
            modBus.addListener(this::gatherDataEvent);
        }
    }

    private void gatherDataEvent(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
        TestItemTagProvider itemTagProvider = new TestItemTagProvider(packOutput, lookupProvider, TrimmedTest.MODID, event.getExistingFileHelper());
        generator.addProvider(event.includeServer(), itemTagProvider);

        LanguageProvider langProvider = new LanguageProvider(packOutput, TrimmedTest.MODID, "en_us") {
            @Override
            protected void addTranslations() {}
        };

        TrimDatagenSuite.create(event, TrimmedTest.MODID, langProvider::add)
                .makeMaterial(ModTrimMaterials.ECHO, Items.ECHO_SHARD, 0x0A4F5F)
                .makeMaterial(ModTrimMaterials.BLAZE, Items.BLAZE_POWDER, 0xFCA100)
                .makeMaterial(ModTrimMaterials.SHELL, Items.NAUTILUS_SHELL, 0xD17E7E)
                .makeMaterial(ModTrimMaterials.PRISMARINE, Items.PRISMARINE_CRYSTALS, 0xB2D5C8)
                .makeMaterial(ModTrimMaterials.GLOW, Items.GLOW_INK_SAC, 0x7EFCBE)
                .makeMaterial(ModTrimMaterials.ADAMANTIUM, ModItems.ADAMANTIUM.asItem(), 0x9d2638, materialConfig -> materialConfig.armorOverride(ModArmorMaterials.ADAMANTIUM, "testmod_adamantium_darker"))
                .makePattern(ModTrimPatterns.SPIRAL, ModItems.SPIRAL_PATTERN.asItem(), false, patternConfig -> patternConfig.createCopyRecipe(Items.NAUTILUS_SHELL));

//        new TestTrimDatagenSuite(event, TrimmedTest.MODID, langProvider::add);

        generator.addProvider(event.includeClient(), langProvider);
        generator.addProvider(event.includeClient(), new TestModelProvider(packOutput, event.getExistingFileHelper()));

        generator.addProvider(event.includeClient(), new TestItemOverrideProvider(packOutput));
        generator.addProvider(event.includeClient(), new TestModelSourceProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientTagProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientItemTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientCustomObjTagProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new TestClientMapProvider(packOutput, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new ClientBlockProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
//        generator.addProvider(event.includeClient(), new ClientDamageTypeProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
        generator.addProvider(event.includeClient(), new EntityToEntityProvider(packOutput, lookupProvider, event.getExistingFileHelper()));
    }
}
