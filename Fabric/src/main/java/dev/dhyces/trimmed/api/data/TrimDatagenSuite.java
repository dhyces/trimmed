package dev.dhyces.trimmed.api.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import dev.dhyces.trimmed.api.data.maps.ClientMapDataProvider;
import dev.dhyces.trimmed.api.client.UncheckedClientMaps;
import dev.dhyces.trimmed.api.client.UncheckedClientTags;
import dev.dhyces.trimmed.api.data.tags.ClientTagDataProvider;
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
        pack.addProvider((output, registriesFuture) -> {
            return new FabricDynamicRegistryProvider(output, registriesFuture) {
                @Override
                protected void configure(HolderLookup.Provider registries, Entries entries) {
                    patterns.forEach(pair -> entries.add(pair.getFirst(), pair.getSecond()));
                    materials.forEach(pair -> entries.add(pair.getFirst(), pair.getSecond()));
                }

                @Override
                public String getName() {
                    return "TrimDatagenSuite / FabricDynamicRegistryProvider for " + modid;
                }
            };
        });

        pack.addProvider((FabricDataOutput output) -> new FabricRecipeProvider(output) {
            @Override
            public void buildRecipes(RecipeOutput output) {
                trimRecipes.forEach(pair -> pair.getSecond().save(output, pair.getFirst()));
                copyRecipes.forEach(pair -> pair.getSecond().save(output));
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
                        tag(ItemTags.TRIM_TEMPLATES).add(patterns.stream().map(pair -> pair.getSecond().templateItem().value()).toArray(Item[]::new));
                    }
                    if (!materials.isEmpty()) {
                        tag(ItemTags.TRIM_MATERIALS).add(materials.stream().map(pair -> pair.getSecond().ingredient().value()).toArray(Item[]::new));
                    }
                }

                @Override
                public String getName() {
                    return "TrimDatagenSuite / " + super.getName();
                }
            };
        });

        pack.addProvider((FabricDataOutput output) -> {
            return new ClientTagDataProvider(output, modid) {
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
            };
        });

        pack.addProvider((FabricDataOutput output) -> {
            return new ClientMapDataProvider(output, modid) {
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
            };
        });
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
