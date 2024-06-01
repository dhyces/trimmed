package dev.dhyces.trimmed.api.data;

import it.unimi.dsi.fastutil.objects.*;
import net.minecraft.Util;
import net.minecraft.advancements.critereon.InventoryChangeTrigger;
import net.minecraft.core.Holder;
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

    protected Map<ResourceKey<TrimPattern>, TrimPattern> patterns = new Reference2ObjectArrayMap<>();
    protected Map<ResourceLocation, ShapedRecipeBuilder> copyRecipes = new Object2ObjectArrayMap<>();
    protected Map<ResourceLocation, SmithingTrimRecipeBuilder> trimRecipes = new Object2ObjectArrayMap<>();
    protected Map<ResourceKey<TrimMaterial>, TrimMaterial> materials = new Reference2ObjectArrayMap<>();

    protected List<ResourceLocation> patternTextures = new ObjectArrayList<>();
    protected Map<ResourceLocation, String> materialTexturePermutations = new Object2ObjectArrayMap<>();

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
     * Creates a new pattern with decal set to false, as well as add to the "trim_templates" tag, add an entry to the custom patterns
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
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, Supplier<? extends ItemLike> templateItem, boolean isDecal, Consumer<PatternConfig> patternConfigConsumer) {
        return makePattern(patternKey, templateItem.get(), isDecal, patternConfigConsumer);
    }

    /**
     * Creates a new pattern with decal set to false, as well as add to the "trim_templates" tag, add an entry to the
     * custom patterns client-tag, create a smithing trim recipe, and optionally add a lang entry (if a lang provider
     * is provided) and a "copy" recipe (used for duplicating the template).
     * @param patternKey The pattern key
     * @param templateItem The item that is used to obtain this pattern on armor
     */
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, ItemLike templateItem) {
        return makePattern(patternKey, templateItem, false, patternConfig -> {});
    }

    /**
     * Creates a new pattern, as well as add to the "trim_templates" tag, add an entry to the custom patterns
     * client-tag, create a smithing trim recipe, and optionally add a lang entry (if a lang provider is provided)
     * and a "copy" recipe (used for duplicating the template).
     * @param patternKey The pattern key.
     * @param templateItem The item that is used to obtain this pattern on armor.
     * @param isDecal True will make the pattern only render where the armor texture is. False will allow the trim to always render.
     * @param patternConfigConsumer An optional element allowing modification of certain elements.
     */
    public BaseTrimDatagenSuite makePattern(ResourceKey<TrimPattern> patternKey, ItemLike templateItem, boolean isDecal, Consumer<PatternConfig> patternConfigConsumer) {

        String translationKey = Util.makeDescriptionId("trim_pattern", patternKey.location());
        patterns.put(patternKey, new TrimPattern(patternKey.location(), templateItem.asItem().builtInRegistryHolder(), Component.translatable(translationKey), isDecal));

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
            trimRecipes.put(id.withSuffix("_smithing_trim"), makeTrimRecipe(templateItem));
        }

        if (config.copyRecipe != null) {
            copyRecipes.put(id, config.copyRecipe);
        }

        return this;
    }

    protected SmithingTrimRecipeBuilder makeTrimRecipe(ItemLike templateItem) {
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
        materials.put(materialKey, new TrimMaterial(config.assetName, materialItem.asItem().builtInRegistryHolder(), -1.0f, config.overrides, Component.translatable(translationKey).withStyle(config.materialStyle)));

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

        return this;
    }

    public static class PatternConfig {
        protected final ItemLike templateItem;
        protected String mainTranslation;
        protected Set<AltTranslation> altTranslations = new ObjectOpenHashSet<>();
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
        protected final Set<AltTranslation> altTranslations = new ObjectOpenHashSet<>();
        protected final Map<Holder<ArmorMaterial>, String> overrides = new Reference2ObjectOpenHashMap<>();
        protected Style materialStyle;
        protected String mainTranslation;
        protected ResourceLocation paletteTexture;
        protected String assetName;

        private MaterialConfig(ResourceKey<TrimMaterial> materialKey, Style materialStyle) {
            this.materialKey = materialKey;
            this.assetName = materialKey.location().toString().replace(":", "_");
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
         * follow this format "modid_material_name"
         * @param name The new asset name
         * @return This instance for chaining method calls
         */
        public MaterialConfig assetName(String name) {
            assetName = name;
            return this;
        }

        public MaterialConfig armorOverride(Holder<ArmorMaterial> armorMaterial, String assetNameOverride) {
            this.overrides.put(armorMaterial, assetNameOverride);
            return this;
        }

        public MaterialConfig armorOverride(ArmorMaterial armorMaterial, String assetNameOverride) {
            this.armorOverride(BuiltInRegistries.ARMOR_MATERIAL.getResourceKey(armorMaterial).flatMap(BuiltInRegistries.ARMOR_MATERIAL::getHolder).orElseThrow(), assetNameOverride);
            return this;
        }

        public MaterialConfig armorOverride(ResourceKey<ArmorMaterial> armorMaterial, String assetNameOverride) {
            this.armorOverride(BuiltInRegistries.ARMOR_MATERIAL.getHolderOrThrow(armorMaterial), assetNameOverride);
            return this;
        }
    }

    protected record AltTranslation(BiConsumer<String, String> consumer, String translation) {
        public void finish(String key) {
            consumer.accept(key, translation);
        }
    }
}
