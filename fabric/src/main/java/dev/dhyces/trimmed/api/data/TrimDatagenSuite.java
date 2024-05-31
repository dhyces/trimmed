package dev.dhyces.trimmed.api.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import dev.dhyces.trimmed.api.client.ClientKeyResolvers;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.data.map.ClientMapDataProvider;
import dev.dhyces.trimmed.api.client.tag.ClientTags;
import dev.dhyces.trimmed.api.data.tag.ClientTagDataProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.recipes.RecipeOutput;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TrimDatagenSuite extends BaseTrimDatagenSuite {

    protected static final Multimap<ResourceLocation, Pair<String, String>> TRANSLATIONS = HashMultimap.create();

    public TrimDatagenSuite(FabricDataGenerator.Pack pack, String modid, @Nullable String mainLanguageCode) {
        super(modid, (key, translation) -> {
            if (mainLanguageCode != null) {
                TRANSLATIONS.put(new ResourceLocation(modid, mainLanguageCode), Pair.of(key, translation));
            }
        });
        pack.addProvider((output, registriesFuture) ->
            new FabricDynamicRegistryProvider(output, registriesFuture) {
                @Override
                protected void configure(HolderLookup.Provider registries, Entries entries) {
                    patterns.forEach(entries::add);
                    materials.forEach(entries::add);
                }

                @Override
                public String getName() {
                    return "TrimDatagenSuite / FabricDynamicRegistryProvider for " + modid;
                }
            }
        );

        pack.addProvider((FabricDataOutput output, CompletableFuture<HolderLookup.Provider> registriesFuture) -> new FabricRecipeProvider(output, registriesFuture) {
            @Override
            public void buildRecipes(RecipeOutput output) {
                trimRecipes.forEach((id, smithingTrimRecipeBuilder) -> smithingTrimRecipeBuilder.save(output, id));
                copyRecipes.forEach((id, smithingTrimRecipeBuilder) -> smithingTrimRecipeBuilder.save(output));
            }

            @Override
            public String getName() {
                return "TrimDatagenSuite / " + super.getName() + ": " + modid;
            }
        });

        pack.addProvider((output, registriesFuture) -> {
            return new ItemTagsProvider(output, registriesFuture, CompletableFuture.completedFuture(TagsProvider.TagLookup.empty())) {
                @Override
                protected void addTags(HolderLookup.Provider provider) {
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
            };
        });

        pack.addProvider((FabricDataOutput output) ->
            new ClientTagDataProvider<>(output, modid, ClientKeyResolvers.TEXTURE) {
                @Override
                protected void addTags() {
                    if (!patternTextures.isEmpty()) {
                        clientTag(ClientTags.TRIM_PATTERN_TEXTURES).add(patternTextures.toArray(ResourceLocation[]::new));
                    }
                }

                @Override
                public String getName() {
                    return "TrimDatagenSuite / " + super.getName();
                }
            }
        );

        pack.addProvider((FabricDataOutput output) ->
            new ClientMapDataProvider<ResourceLocation>(output, modid, ClientKeyResolvers.TEXTURE) {
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
            }
        );
    }

    public static TrimDatagenSuite create(FabricDataGenerator.Pack pack, String modid) {
        return create(pack, modid, null);
    }

    public static TrimDatagenSuite create(FabricDataGenerator.Pack pack, String modid, @Nullable String mainLanguageCode) {
        return new TrimDatagenSuite(pack, modid, mainLanguageCode);
    }

    /**
     * This must be called in language providers so that translations are generated. Otherwise, modders can handle the
     * translations themselves.
     */
    public void resolveTranslationsFor(String languageCode, FabricLanguageProvider.TranslationBuilder builder) {
        TRANSLATIONS.get(new ResourceLocation(modid, languageCode)).forEach(pair -> builder.add(pair.getFirst(), pair.getSecond()));
    }
}
