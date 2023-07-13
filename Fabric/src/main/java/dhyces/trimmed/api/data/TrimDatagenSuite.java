package dhyces.trimmed.api.data;

import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.client.UncheckedClientTags;
import dhyces.trimmed.api.data.maps.ClientMapDataProvider;
import dhyces.trimmed.api.data.tags.ClientTagDataProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.tags.ItemTagsProvider;
import net.minecraft.data.tags.TagsProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

public class TrimDatagenSuite extends BaseTrimDatagenSuite {
    public TrimDatagenSuite(FabricDataGenerator.Pack pack, String modid, @Nullable BiConsumer<String, String> translationConsumer) {
        super(modid, translationConsumer);
        RegistrySetBuilder builder = new RegistrySetBuilder()
                .add(Registries.TRIM_PATTERN, pContext -> {

                })
                .add(Registries.TRIM_MATERIAL, pContext -> {

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
            public void buildRecipes(Consumer<FinishedRecipe> exporter) {
                trimRecipes.forEach(pair -> pair.getSecond().save(exporter, pair.getFirst()));
                copyRecipes.forEach(pair -> pair.getSecond().save(exporter));
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
                    tag(ItemTags.TRIM_TEMPLATES).add(patterns.stream().map(pair -> pair.getSecond().templateItem().value()).toArray(Item[]::new));
                    tag(ItemTags.TRIM_MATERIALS).add(materials.stream().map(pair -> pair.getSecond().ingredient().value()).toArray(Item[]::new));
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
                    clientTag(UncheckedClientTags.CUSTOM_TRIM_PATTERN_TEXTURES).add(patternTextures.toArray(ResourceLocation[]::new));
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
                    map(UncheckedClientMaps.CUSTOM_TRIM_PERMUTATIONS).putAll(materialTexturePermutations);
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
}
