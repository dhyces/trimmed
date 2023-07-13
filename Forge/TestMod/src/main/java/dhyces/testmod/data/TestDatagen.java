package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import dhyces.testmod.TrimmedTest;
import dhyces.testmod.data.trimmed.*;
import dhyces.testmod.data.trimmed.registrymaps.ClientBlockProvider;
import dhyces.testmod.data.trimmed.registrymaps.ClientDamageTypeProvider;
import dhyces.testmod.data.trimmed.registrymaps.EntityToEntityProvider;
import dhyces.testmod.data.trimmed.registrytags.TestClientCustomObjTagProvider;
import dhyces.testmod.data.trimmed.registrytags.TestClientItemTagProvider;
import dhyces.testmod.item.AdamantiumArmorItem;
import dhyces.trimmed.api.data.BaseTrimDatagenSuite;
import dhyces.trimmed.api.data.TrimDatagenSuite;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimMaterials;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.common.data.LanguageProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import java.util.Collections;
import java.util.Set;
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

        TrimDatagenSuite suite = new TrimDatagenSuite(event, TrimmedTest.MODID, langProvider::add);
        suite.makeMaterial(ModTrimMaterials.ECHO, Items.ECHO_SHARD, 0x0A4F5F)
                .makeMaterial(ModTrimMaterials.BLAZE, Items.BLAZE_POWDER, 0xFCA100)
                .makeMaterial(ModTrimMaterials.SHELL, Items.NAUTILUS_SHELL, 0xD17E7E)
                .makeMaterial(ModTrimMaterials.PRISMARINE, Items.PRISMARINE_CRYSTALS, 0xB2D5C8)
                .makeMaterial(ModTrimMaterials.GLOW, Items.GLOW_INK_SAC, 0x7EFCBE)
                .makeMaterial(ModTrimMaterials.ADAMANTIUM, ModItems.ADAMANTIUM, 0x9d2638,
                        materialConfig -> {
                    materialConfig.armorOverride(AdamantiumArmorItem.AdamantiumArmorMaterial.INSTANCE, "trimmed_testmod-adamantium_darker");
                })
                .makePattern(ModTrimPatterns.SPIRAL, ModItems.SPIRAL_PATTERN, patternConfig -> patternConfig.createCopyRecipe(Items.NAUTILUS_SHELL));

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
