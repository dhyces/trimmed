package dev.dhyces.trimmed.api.data;

import dev.dhyces.trimmed.api.client.UncheckedClientMaps;
import dev.dhyces.trimmed.api.client.UncheckedClientTags;
import dev.dhyces.trimmed.api.data.maps.ClientMapDataProvider;
import dev.dhyces.trimmed.api.data.tags.ClientTagDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class TrimDatagenSuite extends BaseTrimDatagenSuite {

    public TrimDatagenSuite(GatherDataEvent event, String modid) {
        this(event, modid, null);
    }

    public TrimDatagenSuite(GatherDataEvent event, String modid, @Nullable BiConsumer<String, String> translationConsumer) {
        super(modid, translationConsumer);
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = event.getGenerator().getPackOutput();
        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.TRIM_PATTERN, pContext -> {
                    patterns.forEach(pair -> pContext.register(pair.getFirst(), pair.getSecond()));
                })
                .add(Registries.TRIM_MATERIAL, pContext -> {
                    materials.forEach(pair -> pContext.register(pair.getFirst(), pair.getSecond()));
                });
        generator.addProvider(event.includeServer(), (DataProvider.Factory<? extends DataProvider>) pOutput -> new RegistriesDatapackGenerator(packOutput, RegistryPatchGenerator.createLookup(event.getLookupProvider(), builder).thenApply(RegistrySetBuilder.PatchedRegistries::patches), Set.of(modid)) {
            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName() + " " + modid;
            }
        });
        generator.addProvider(event.includeServer(), new RecipeProvider(packOutput) {
            @Override
            protected void buildRecipes(RecipeOutput output) {
                trimRecipes.forEach(pair -> pair.getSecond().save(output, pair.getFirst()));
                copyRecipes.forEach(pair -> pair.getSecond().save(output));
            }

            public String getName() {
                return "TrimDatagenSuite / " + super.getName() + ": " + modid;
            }
        });
        generator.addProvider(event.includeServer(), new ItemTagsProvider(packOutput, event.getLookupProvider(), CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), modid, event.getExistingFileHelper()) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {
                if (!patterns.isEmpty()) {
                    tag(ItemTags.TRIM_TEMPLATES).add(patterns.stream().map(pair -> pair.getSecond().templateItem().get()).toArray(Item[]::new));
                }
                if (!materials.isEmpty()) {
                    tag(ItemTags.TRIM_MATERIALS).add(materials.stream().map(pair -> pair.getSecond().ingredient().get()).toArray(Item[]::new));
                }
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName();
            }
        });

        generator.addProvider(event.includeClient(), new ClientTagDataProvider(packOutput, modid, event.getExistingFileHelper()) {
            @Override
            protected void addTags() {
                if (!patternTextures.isEmpty()) {
                    clientTag(UncheckedClientTags.CUSTOM_TRIM_PATTERN_TEXTURES).add(patternTextures.toArray(ResourceLocation[]::new));
                }
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName();
            }
        });
        generator.addProvider(event.includeClient(), new ClientMapDataProvider(packOutput, modid, event.getExistingFileHelper()) {
            @Override
            protected void addMaps() {
                if (!materialTexturePermutations.isEmpty()) {
                    map(UncheckedClientMaps.CUSTOM_TRIM_PERMUTATIONS).putAll(materialTexturePermutations);
                }
                armorMaterialOverrides.forEach((trimMaterialResourceKey, armorMaterialOverride) -> {
                    map(UncheckedClientMaps.armorMaterialOverride(trimMaterialResourceKey))
                            .put(armorMaterialOverride.armorMaterial(), armorMaterialOverride.overrideSuffix());
                });
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName();
            }
        });
    }

    public static TrimDatagenSuite create(GatherDataEvent event, String modid) {
        return new TrimDatagenSuite(event, modid);
    }

    public static TrimDatagenSuite create(GatherDataEvent event, String modid, @Nullable BiConsumer<String, String> translationConsumer) {
        return new TrimDatagenSuite(event, modid, translationConsumer);
    }
}
