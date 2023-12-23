package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import dhyces.testmod.TrimmedTest;
import dhyces.testmod.data.trimmed.TestClientMapProvider;
import dhyces.testmod.data.trimmed.TestClientTagProvider;
import dhyces.testmod.data.trimmed.TestItemOverrideProvider;
import dhyces.testmod.data.trimmed.registrymaps.ClientBlockProvider;
import dhyces.testmod.data.trimmed.registrymaps.ClientDamageTypeProvider;
import dhyces.testmod.data.trimmed.registrymaps.EntityToEntityProvider;
import dhyces.testmod.data.trimmed.registrytags.TestClientCustomObjTagProvider;
import dhyces.testmod.data.trimmed.registrytags.TestClientItemTagProvider;
import dhyces.testmod.item.AdamantiumArmorItem;
import dev.dhyces.trimmed.api.data.TrimDatagenSuite;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.world.item.Items;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.concurrent.CompletableFuture;

public class TestDatagen {

    public static void init(IEventBus modBus) {
        modBus.addListener(TestDatagen::gatherDataEvent);
    }

    private static void gatherDataEvent(final GatherDataEvent event) {
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
                .makeMaterial(ModTrimMaterials.ADAMANTIUM, ModItems.ADAMANTIUM, 0x9d2638,
                        materialConfig -> {
                    materialConfig.armorOverride(AdamantiumArmorItem.AdamantiumArmorMaterial.INSTANCE, "trimmed_testmod-adamantium_darker");
                })
                .makePattern(ModTrimPatterns.SPIRAL, ModItems.SPIRAL_PATTERN, patternConfig -> patternConfig.createCopyRecipe(Items.NAUTILUS_SHELL));

//        new TestTrimDatagenSuite(event, TrimmedTest.MODID, langProvider::add);

        generator.addProvider(event.includeClient(), langProvider);
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
