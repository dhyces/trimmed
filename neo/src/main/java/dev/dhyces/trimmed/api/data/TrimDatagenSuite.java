package dev.dhyces.trimmed.api.data;

import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.tag.ClientTags;
import dev.dhyces.trimmed.api.data.map.ClientMapDataProvider;
import dev.dhyces.trimmed.api.data.tag.ClientTagDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.recipes.RecipeProvider;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.data.event.GatherDataEvent;
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
        CompletableFuture<HolderLookup.Provider> lookup = event.getLookupProvider();
        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.TRIM_PATTERN, pContext -> {
                    patterns.forEach(pContext::register);
                })
                .add(Registries.TRIM_MATERIAL, pContext -> {
                    materials.forEach(pContext::register);
                });
        lookup = generator.addProvider(event.includeServer(), (DataProvider.Factory<? extends DatapackBuiltinEntriesProvider>) pOutput -> new DatapackBuiltinEntriesProvider(packOutput, event.getLookupProvider(), builder, Set.of(modid)) {
            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName() + " " + modid;
            }
        }).getRegistryProvider();
        generator.addProvider(event.includeServer(), new RecipeProvider.Runner(packOutput, lookup) {
            @Override
            protected RecipeProvider createRecipeProvider(HolderLookup.Provider lookupProvider, RecipeOutput output) {
                return new RecipeProvider(lookupProvider, output) {
                    @Override
                    protected void buildRecipes() {
                        trimRecipes.forEach((id, smithingTrimRecipeBuilder) -> smithingTrimRecipeBuilder.save(output, ResourceKey.create(Registries.RECIPE, id)));
                        copyRecipes.forEach((id, smithingTrimRecipeBuilder) -> smithingTrimRecipeBuilder.save(output));
                    }
                };
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / Recipes: " + modid;
            }
        });
        generator.addProvider(event.includeServer(), new ItemTagsProvider(packOutput, lookup, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty()), modid, event.getExistingFileHelper()) {
            @Override
            protected void addTags(HolderLookup.Provider pProvider) {
                if (!patterns.isEmpty()) {
                    tag(ItemTags.TRIM_TEMPLATES).add(patterns.values().stream().map(trimPattern -> trimPattern.templateItem().value()).toArray(Item[]::new));
                }
                if (!materials.isEmpty()) {
                    tag(ItemTags.TRIM_MATERIALS).add(materials.values().stream().map(trimMaterial -> trimMaterial.ingredient().value()).toArray(Item[]::new));
                }
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName();
            }
        });

        generator.addProvider(event.includeClient(), new ClientTagDataProvider<>(packOutput, modid, ClientKeyResolvers.TEXTURE, event.getExistingFileHelper()) {
            @Override
            protected void addTags() {
                if (!patternTextures.isEmpty()) {
                    tag(ClientTags.TRIM_PATTERN_TEXTURES).add(patternTextures.toArray(ResourceLocation[]::new));
                }
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName();
            }
        });
        generator.addProvider(event.includeClient(), new ClientMapDataProvider<ResourceLocation>(packOutput, modid, event.getExistingFileHelper()) {
            @Override
            protected void addMaps() {
                if (!materialTexturePermutations.isEmpty()) {
                    map(ClientMapKeys.MATERIAL_SUFFIXES).putAll(materialTexturePermutations);
                }
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
