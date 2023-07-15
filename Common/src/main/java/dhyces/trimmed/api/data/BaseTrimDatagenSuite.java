package dhyces.trimmed.api.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.mojang.datafixers.util.Pair;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.recipes.RecipeCategory;
import net.minecraft.data.recipes.ShapedRecipeBuilder;
import net.minecraft.data.recipes.SmithingTrimRecipeBuilder;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.ItemLike;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public abstract class BaseTrimDatagenSuite {

    protected static Pattern replacerPattern = Pattern.compile("(\\b[a-z](?!\\s))");

    @Nullable
    private final BiConsumer<String, String> mainTranslationConsumer;
    protected final String modid;

    protected List<Pair<ResourceKey<TrimPattern>, TrimPattern>> patterns = new ArrayList<>();
    protected List<Pair<ResourceLocation, ShapedRecipeBuilder>> copyRecipes = new ArrayList<>();
    protected List<Pair<ResourceLocation, SmithingTrimRecipeBuilder>> trimRecipes = new ArrayList<>();
    protected List<Pair<ResourceKey<TrimMaterial>, TrimMaterial>> materials = new ArrayList<>();

    protected List<ResourceLocation> patternTextures = new ArrayList<>();
    protected Map<ResourceLocation, String> materialTexturePermutations = new LinkedHashMap<>();
    protected Multimap<ResourceKey<TrimMaterial>, ArmorMaterialOverride> armorMaterialOverrides = HashMultimap.create();

    public BaseTrimDatagenSuite(String modid, @Nullable BiConsumer<String, String> translationConsumer) {
        this.modid = modid;
        this.mainTranslationConsumer = translationConsumer;
        generate();
    }

    /**
     * Use this if you wish to extend the class and generate in one method
     */
    public void generate() {

    }

    /**
     * Creates a new pattern, as well as add to the "trim_templates" tag, add an entry to the custom patterns
     * client-tag, create a smithing trim recipe, and optionally add a lang entry (if a lang provider is provided)
     * and a "copy" recipe (used for duplicating the template).
     * @param patternKey The pattern key
     * @param templateItem The item that is used to obtain this pattern on armor
     */
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, Supplier<? extends ItemLike> templateItem) {
        return makePattern(patternKey, templateItem.get());
    }

    /**
     * Creates a new pattern, as well as add to the "trim_templates" tag, add an entry to the custom patterns
     * client-tag, create a smithing trim recipe, and optionally add a lang entry (if a lang provider is provided)
     * and a "copy" recipe (used for duplicating the template).
     * @param patternKey The pattern key
     * @param templateItem The item that is used to obtain this pattern on armor
     * @param patternConfigConsumer An optional element allowing modification of certain elements
     */
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, Supplier<? extends ItemLike> templateItem, Consumer<PatternConfig> patternConfigConsumer) {
        return makePattern(patternKey, templateItem.get(), patternConfigConsumer);
    }

    /**
     * Creates a new pattern, as well as add to the "trim_templates" tag, add an entry to the custom patterns
     * client-tag, create a smithing trim recipe, and optionally add a lang entry (if a lang provider is provided)
     * and a "copy" recipe (used for duplicating the template).
     * @param patternKey The pattern key
     * @param templateItem The item that is used to obtain this pattern on armor
     */
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, ItemLike templateItem) {
        return makePattern(patternKey, templateItem, patternConfig -> {});
    }

    /**
     * Creates a new pattern, as well as add to the "trim_templates" tag, add an entry to the custom patterns
     * client-tag, create a smithing trim recipe, and optionally add a lang entry (if a lang provider is provided)
     * and a "copy" recipe (used for duplicating the template).
     * @param patternKey The pattern key
     * @param templateItem The item that is used to obtain this pattern on armor
     * @param patternConfigConsumer An optional element allowing modification of certain elements
     */
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, ItemLike templateItem, Consumer<PatternConfig> patternConfigConsumer) {

        String translationKey = Util.makeDescriptionId("trim_pattern", patternKey.location());
        patterns.add(Pair.of(patternKey, new TrimPattern(patternKey.location(), templateItem.asItem().builtInRegistryHolder(), Component.translatable(translationKey))));


        PatternConfig config = new PatternConfig(templateItem);
        patternConfigConsumer.accept(config);

        if (mainTranslationConsumer != null) {
            String translation;
            if (config.mainTranslation == null) {
                // Generated en_us translation
                translation = replacerPattern.matcher(patternKey.location().getPath().replace("_", " "))
                        .replaceAll(matcher -> matcher.group().toUpperCase()) + " Armor Trim";
            } else {
                translation = config.mainTranslation;
            }

            mainTranslationConsumer.accept(translationKey, translation);
        }

        config.altTranslations.forEach(altTranslation -> altTranslation.finish(translationKey));

        if (config.mainTexture == null) {
            patternTextures.add(new ResourceLocation(patternKey.location().getNamespace(), "trims/models/armor/" + patternKey.location().getPath()));
            patternTextures.add(new ResourceLocation(patternKey.location().getNamespace(), "trims/models/armor/" + patternKey.location().getPath() + "_leggings"));
        } else {
            patternTextures.add(config.mainTexture);
            patternTextures.add(config.leggingsTexture);
        }

        ResourceLocation id = BuiltInRegistries.ITEM.getKey(templateItem.asItem());

        if (!config.omitTrimRecipe) {
            trimRecipes.add(Pair.of(id.withSuffix("_smithing_trim"), makeTrimRecipe(templateItem)));
        }

        if (config.copyRecipe != null) {
            copyRecipes.add(Pair.of(id, config.copyRecipe));
        }

        return this;
    }

    private SmithingTrimRecipeBuilder makeTrimRecipe(ItemLike templateItem) {
        return SmithingTrimRecipeBuilder.smithingTrim(
                        Ingredient.of(templateItem),
                        Ingredient.of(ItemTags.TRIMMABLE_ARMOR),
                        Ingredient.of(ItemTags.TRIM_MATERIALS),
                        RecipeCategory.MISC
                )
                .unlocks("has_smithing_trim_template", InventoryChangeTrigger.TriggerInstance.hasItems(templateItem));
    }

    /**
     * Creates a new material, as well as add to the "trim_materials" tag, add an entry to the custom material
     * permutations client-map, and optionally add a lang entry (if a lang provider is provided).
     * @param materialKey The key for your material
     * @param materialItem The item that is attributed to this material
     * @param color The color to style the component shown in the tooltip
     */
    public BaseTrimDatagenSuite makeMaterial(ResourceKey<TrimMaterial> materialKey, Supplier<? extends ItemLike> materialItem, int color) {
        return makeMaterial(materialKey, materialItem.get(), color);
    }

    /**
     * Creates a new material, as well as add to the "trim_materials" tag, add an entry to the custom material
     * permutations client-map, and optionally add a lang entry (if a lang provider is provided).
     * @param materialKey The key for your material
     * @param materialItem The item that is attributed to this material
     * @param color The color to style the component shown in the tooltip
     * @param materialConfigConsumer An optional consumer allowing modification of certain elements
     */
    public BaseTrimDatagenSuite makeMaterial(ResourceKey<TrimMaterial> materialKey, Supplier<? extends ItemLike> materialItem, int color, Consumer<MaterialConfig> materialConfigConsumer) {
        return makeMaterial(materialKey, materialItem.get(), color, materialConfigConsumer);
    }

    /**
     * Creates a new material, as well as add to the "trim_materials" tag, add an entry to the custom material
     * permutations client-map, and optionally add a lang entry (if a lang provider is provided).
     * @param materialKey The key for your material
     * @param materialItem The item that is attributed to this material
     * @param color The color to style the component shown in the tooltip
     */
    public BaseTrimDatagenSuite makeMaterial(ResourceKey<TrimMaterial> materialKey, ItemLike materialItem, int color) {
        return makeMaterial(materialKey, materialItem, color, materialConfig -> {});
    }

    /**
     * Creates a new material, as well as add to the "trim_materials" tag, add an entry to the custom material
     * permutations client-map, and optionally add a lang entry (if a lang provider is provided).
     * @param materialKey The key for your material
     * @param materialItem The item that is attributed to this material
     * @param color The color to style the component shown in the tooltip
     * @param materialConfigConsumer An optional consumer allowing modification of certain elements
     */
    public BaseTrimDatagenSuite makeMaterial(ResourceKey<TrimMaterial> materialKey, ItemLike materialItem, int color, Consumer<MaterialConfig> materialConfigConsumer) {

        MaterialConfig config = new MaterialConfig(materialKey, Style.EMPTY.withColor(color));
        materialConfigConsumer.accept(config);

        String translationKey = Util.makeDescriptionId("trim_pattern", materialKey.location());
        materials.add(Pair.of(materialKey, new TrimMaterial(config.assetName, materialItem.asItem().builtInRegistryHolder(), -1.0f, Map.of(), Component.translatable(translationKey).withStyle(config.materialStyle))));

        if (mainTranslationConsumer != null) {
            String translation;
            if (config.mainTranslation == null) {
                // Generated en_us translation
                translation = replacerPattern.matcher(materialKey.location().getPath().replace("_", " "))
                        .replaceAll(matcher -> matcher.group().toUpperCase()) + " Material";
            } else {
                translation = config.mainTranslation;
            }

            mainTranslationConsumer.accept(translationKey, translation);
        }

        config.altTranslations.forEach(altTranslation -> altTranslation.finish(translationKey));

        if (config.paletteTexture != null) {
            materialTexturePermutations.put(config.paletteTexture, config.assetName);
        } else {
            ResourceLocation key = materialKey.location().withPrefix("trims/color_palettes/");
            materialTexturePermutations.put(key, config.assetName);
        }

        if (!config.armorOverrides.isEmpty()) {
            config.armorOverrides.forEach(armorMaterialOverride -> {
                armorMaterialOverrides.put(materialKey, armorMaterialOverride);
                materialTexturePermutations.put(armorMaterialOverride.textureLocation, armorMaterialOverride.overrideSuffix);
            });
        }

        return this;
    }

    public static class PatternConfig {
        protected final ItemLike templateItem;
        protected String mainTranslation;
        protected Set<AltTranslation> altTranslations = new HashSet<>();
        protected ResourceLocation mainTexture;
        protected ResourceLocation leggingsTexture;
        protected ShapedRecipeBuilder copyRecipe;
        protected boolean omitTrimRecipe;

        protected PatternConfig(ItemLike templateItem) {
            this.templateItem = templateItem;
        }

        /**
         * The default lang entry is en_us, generated from the pattern id. This allows users to change the lang entry
         * associated with the translation consumer passed in from the constructor.
         */
        public PatternConfig langEntry(String translation) {
            mainTranslation = translation;
            return this;
        }

        /**
         * Allows users to specify alternate lang entries for other language providers
         */
        public PatternConfig langEntry(BiConsumer<String, String> translationConsumer, String translation) {
            altTranslations.add(new AltTranslation(translationConsumer, translation));
            return this;
        }

        /**
         * The generated paths are "modid:trims/models/armor/pattern" and "modid:trims/models/armor/pattern_leggings".
         * This method can be used if the textures are located elsewhere.
         */
        public PatternConfig textureLocations(ResourceLocation main, ResourceLocation leggings) {
            mainTexture = main;
            leggingsTexture = leggings;
            return this;
        }

        /**
         * Creates a default "copy" recipe to duplicate the template.
         * #S#    # = minecraft:diamond
         * #C#    C = baseItem
         * ###    S = templateItem
         */
        public PatternConfig createCopyRecipe(ItemLike baseItem) {
            copyRecipe = ShapedRecipeBuilder.shaped(RecipeCategory.MISC, templateItem, 2)
                    .define('#', Items.DIAMOND)
                    .define('C', baseItem)
                    .define('S', templateItem)
                    .pattern("#S#")
                    .pattern("#C#")
                    .pattern("###")
                    .unlockedBy(
                            "has_" + BuiltInRegistries.ITEM.getKey(templateItem.asItem()).getPath(),
                            InventoryChangeTrigger.TriggerInstance.hasItems(templateItem)
                    );
            return this;
        }

        /**
         * Sets a flag that prevents a default smithing trim recipe from being generated.
         */
        public PatternConfig omitTrimRecipe() {
            omitTrimRecipe = true;
            return this;
        }
    }

    public static class MaterialConfig {
        protected final ResourceKey<TrimMaterial> materialKey;
        protected Style materialStyle;
        protected String mainTranslation;
        protected Set<AltTranslation> altTranslations = new HashSet<>();
        protected ResourceLocation paletteTexture;
        protected List<ArmorMaterialOverride> armorOverrides = new ArrayList<>();
        protected String assetName;

        private MaterialConfig(ResourceKey<TrimMaterial> materialKey, Style materialStyle) {
            this.materialKey = materialKey;
            this.assetName = materialKey.location().toString().replace(':', '-');
            this.materialStyle = materialStyle;
        }

        /**
         * The default lang entry is en_us, generated from the pattern id. This allows users to change the lang entry
         * associated with the translation consumer passed in from the constructor.
         */
        public MaterialConfig langEntry(String translation) {
            this.mainTranslation = translation;
            return this;
        }

        /**
         * Allows users to specify alternate lang entries for other language providers
         */
        public MaterialConfig langEntry(BiConsumer<String, String> translationConsumer, String translation) {
            altTranslations.add(new AltTranslation(translationConsumer, translation));
            return this;
        }

        /**
         * Allows users to change the texture location from the generated default located in
         * "modid:trims/color_palettes/material"
         */
        public MaterialConfig colorPaletteTexture(ResourceLocation paletteTexture) {
            this.paletteTexture = paletteTexture;
            return this;
        }

        /**
         * Override for {@link MaterialConfig#armorOverride(ArmorMaterial, ResourceLocation, String)}, but it assumes
         * the texture location is "{modid}:trims/color_palettes/{material}_darker".
         * @param overrideSuffix Suffix for the override. It's encouraged to use the "modid-suffix" format.
         */
        public MaterialConfig armorOverride(ArmorMaterial armorMaterial, String overrideSuffix) {
            return armorOverride(armorMaterial, materialKey.location().withPath(s -> "trims/color_palettes/" + s + "_darker"), overrideSuffix);
        }

        /**
         * Allows users to specify texture overrides for armor materials, for example, using a darker texture when the
         * material is applied to an armor of the same color/crafting material. Vanilla hardcodes an enum into their
         * TrimMaterial class, which means that enum extension would be mandatory for modders and everyone would have
         * to migrate their 'ArmorMaterial's to it. Trimmed provides an alternate system outside the material class
         * as a resource-pack element.
         * @param overrideSuffix Suffix for the override. It's encouraged to use the "modid-suffix" format.
         */
        public MaterialConfig armorOverride(ArmorMaterial armorMaterial, ResourceLocation textureLocation, String overrideSuffix) {
            armorOverrides.add(new ArmorMaterialOverride(new ResourceLocation(armorMaterial.getName()), textureLocation, overrideSuffix));
            return this;
        }

        /**
         * Allows modification of the style used in the material's component. This style is already colored with what
         * was passed into the {@link BaseTrimDatagenSuite#makeMaterial(ResourceKey, ItemLike, int, Consumer)} method.
         * Vanilla only applies color to their components, but modders may want to do more.
         */
        public MaterialConfig style(UnaryOperator<Style> styleOperator) {
            this.materialStyle = styleOperator.apply(materialStyle);
            return this;
        }

        /**
         * Changes the asset name for the material. Highly discouraged to change it from the generated name, as it
         * prefixes the name with the modid to prevent collision with other sources. If changed, it is encouraged to
         * follow this format "modid-material_name"
         * @param name The new asset name
         * @return This instance for chaining method calls
         */
        public MaterialConfig assetName(String name) {
            assetName = name;
            return this;
        }
    }

    record AltTranslation(BiConsumer<String, String> consumer, String translation) {
        public void finish(String key) {
            consumer.accept(key, translation);
        }
    }

    record ArmorMaterialOverride(ResourceLocation armorMaterial, ResourceLocation textureLocation, String overrideSuffix) {}
}
