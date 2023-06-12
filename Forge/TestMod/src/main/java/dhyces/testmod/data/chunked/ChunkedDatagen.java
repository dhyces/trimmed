package dhyces.testmod.data.chunked;

import dhyces.testmod.TrimmedTest;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.common.data.ExistingFileHelper;
import net.minecraftforge.data.event.GatherDataEvent;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

public class ChunkedDatagen {
    private static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
            .add(Registries.TRIM_PATTERN, ChunkedDatagen::bootstrap);

    public static void datagen(final GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
        ExistingFileHelper fileHelper = event.getExistingFileHelper();
        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

        generator.addProvider(true, new PackMetadataGenerator(packOutput));
        generator.addProvider(event.includeServer(), new DatapackBuiltinEntriesProvider(packOutput, lookupProvider, BUILDER, Set.of("chunkedpattern")));
        generator.addProvider(event.includeServer(), new ItemTagProvider(packOutput, lookupProvider, fileHelper));
        generator.addProvider(event.includeServer(), new ChunkedRecipeProvider(packOutput));
    }

    public static final ResourceKey<TrimPattern> CHUNKED = ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation("chunkedpattern", "chunked"));

    private static void bootstrap(BootstapContext<TrimPattern> pContext) {
        pContext.register(CHUNKED, new TrimPattern(new ResourceLocation("chunkedpattern", "chunked"), Items.GRASS_BLOCK.builtInRegistryHolder(), Component.translatable("chunkedpattern.trim_pattern.chunked")));
    }
}
