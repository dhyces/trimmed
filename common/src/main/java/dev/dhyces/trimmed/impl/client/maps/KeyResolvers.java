package dev.dhyces.trimmed.impl.client.maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.decoration.PaintingVariant;
import net.minecraft.world.entity.npc.VillagerProfession;
import net.minecraft.world.entity.npc.VillagerType;
import net.minecraft.world.entity.schedule.Activity;
import net.minecraft.world.entity.schedule.Schedule;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.alchemy.Potion;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.LevelStem;
import net.minecraft.world.level.gameevent.GameEvent;
import net.minecraft.world.level.gameevent.PositionSourceType;
import net.minecraft.world.level.levelgen.DensityFunction;
import net.minecraft.world.level.levelgen.SurfaceRules;
import net.minecraft.world.level.levelgen.blockpredicates.BlockPredicateType;
import net.minecraft.world.level.levelgen.carver.WorldCarver;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.featuresize.FeatureSizeType;
import net.minecraft.world.level.levelgen.feature.foliageplacers.FoliagePlacerType;
import net.minecraft.world.level.levelgen.feature.rootplacers.RootPlacerType;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProviderType;
import net.minecraft.world.level.levelgen.feature.treedecorators.TreeDecoratorType;
import net.minecraft.world.level.levelgen.feature.trunkplacers.TrunkPlacerType;
import net.minecraft.world.level.levelgen.heightproviders.HeightProviderType;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePieceType;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacementType;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElementType;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasBinding;
import net.minecraft.world.level.levelgen.structure.templatesystem.PosRuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.RuleTestType;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureProcessorType;
import net.minecraft.world.level.levelgen.structure.templatesystem.rule.blockentity.RuleBlockEntityModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.saveddata.maps.MapDecorationType;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.world.level.storage.loot.functions.LootItemFunctionType;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.predicates.LootItemConditionType;
import net.minecraft.world.level.storage.loot.providers.nbt.LootNbtProviderType;
import net.minecraft.world.level.storage.loot.providers.number.LootNumberProviderType;
import net.minecraft.world.level.storage.loot.providers.score.LootScoreProviderType;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

public final class KeyResolvers {
    private static final BiMap<ResourceLocation, KeyResolver<?>> RESOLVERS = HashBiMap.create();
    static {
        // TODO: Add data pack registry resolvers
        for (Registry<?> registry : BuiltInRegistries.REGISTRY) {
            RESOLVERS.put(registry.key().location(), KeyResolver.RegistryWrapper.createStatic(registry));
        }
    }

    public static final KeyResolver.RegistryWrapper<Activity> ACTIVITY = getRegistryResolver(Registries.ACTIVITY);
    public static final KeyResolver.RegistryWrapper<Attribute> ATTRIBUTE = getRegistryResolver(Registries.ATTRIBUTE);
//    public static final KeyResolver.RegistryWrapper<BannerPattern> BANNER_PATTERN = getRegistryResolver(Registries.BANNER_PATTERN);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends BiomeSource>> BIOME_SOURCE = getRegistryResolver(Registries.BIOME_SOURCE);
    public static final KeyResolver.RegistryWrapper<Block> BLOCK = getRegistryResolver(Registries.BLOCK);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends Block>> BLOCK_TYPE = getRegistryResolver(Registries.BLOCK_TYPE);
    public static final KeyResolver.RegistryWrapper<BlockEntityType<?>> BLOCK_ENTITY_TYPE = getRegistryResolver(Registries.BLOCK_ENTITY_TYPE);
    public static final KeyResolver.RegistryWrapper<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = getRegistryResolver(Registries.BLOCK_PREDICATE_TYPE);
    public static final KeyResolver.RegistryWrapper<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = getRegistryResolver(Registries.BLOCK_STATE_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<WorldCarver<?>> CARVER = getRegistryResolver(Registries.CARVER);
    public static final KeyResolver.RegistryWrapper<CatVariant> CAT_VARIANT = getRegistryResolver(Registries.CAT_VARIANT);
//    public static final KeyResolver.RegistryWrapper<WolfVariant> WOLF_VARIANT = getRegistryResolver(Registries.WOLF_VARIANT);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR = getRegistryResolver(Registries.CHUNK_GENERATOR);
    public static final KeyResolver.RegistryWrapper<ChunkStatus> CHUNK_STATUS = getRegistryResolver(Registries.CHUNK_STATUS);
    public static final KeyResolver.RegistryWrapper<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = getRegistryResolver(Registries.COMMAND_ARGUMENT_TYPE);
    public static final KeyResolver.RegistryWrapper<CreativeModeTab> CREATIVE_MODE_TAB = getRegistryResolver(Registries.CREATIVE_MODE_TAB);
    public static final KeyResolver.RegistryWrapper<ResourceLocation> CUSTOM_STAT = getRegistryResolver(Registries.CUSTOM_STAT);
//    public static final KeyResolver.RegistryWrapper<DamageType> DAMAGE_TYPE = getRegistryResolver(Registries.DAMAGE_TYPE);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = getRegistryResolver(Registries.DENSITY_FUNCTION_TYPE);
    public static final KeyResolver.RegistryWrapper<Enchantment> ENCHANTMENT = getRegistryResolver(Registries.ENCHANTMENT);
    public static final KeyResolver.RegistryWrapper<EntityType<?>> ENTITY_TYPE = getRegistryResolver(Registries.ENTITY_TYPE);
    public static final KeyResolver.RegistryWrapper<Feature<?>> FEATURE = getRegistryResolver(Registries.FEATURE);
    public static final KeyResolver.RegistryWrapper<FeatureSizeType<?>> FEATURE_SIZE_TYPE = getRegistryResolver(Registries.FEATURE_SIZE_TYPE);
    public static final KeyResolver.RegistryWrapper<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = getRegistryResolver(Registries.FLOAT_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<Fluid> FLUID = getRegistryResolver(Registries.FLUID);
    public static final KeyResolver.RegistryWrapper<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = getRegistryResolver(Registries.FOLIAGE_PLACER_TYPE);
    public static final KeyResolver.RegistryWrapper<FrogVariant> FROG_VARIANT = getRegistryResolver(Registries.FROG_VARIANT);
    public static final KeyResolver.RegistryWrapper<GameEvent> GAME_EVENT = getRegistryResolver(Registries.GAME_EVENT);
    public static final KeyResolver.RegistryWrapper<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = getRegistryResolver(Registries.HEIGHT_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<Instrument> INSTRUMENT = getRegistryResolver(Registries.INSTRUMENT);
    public static final KeyResolver.RegistryWrapper<IntProviderType<?>> INT_PROVIDER_TYPE = getRegistryResolver(Registries.INT_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<Item> ITEM = getRegistryResolver(Registries.ITEM);
    public static final KeyResolver.RegistryWrapper<LootItemConditionType> LOOT_CONDITION_TYPE = getRegistryResolver(Registries.LOOT_CONDITION_TYPE);
    public static final KeyResolver.RegistryWrapper<LootItemFunctionType<?>> LOOT_FUNCTION_TYPE = getRegistryResolver(Registries.LOOT_FUNCTION_TYPE);
    public static final KeyResolver.RegistryWrapper<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = getRegistryResolver(Registries.LOOT_NBT_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = getRegistryResolver(Registries.LOOT_NUMBER_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = getRegistryResolver(Registries.LOOT_POOL_ENTRY_TYPE);
    public static final KeyResolver.RegistryWrapper<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = getRegistryResolver(Registries.LOOT_SCORE_PROVIDER_TYPE);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = getRegistryResolver(Registries.MATERIAL_CONDITION);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = getRegistryResolver(Registries.MATERIAL_RULE);
    public static final KeyResolver.RegistryWrapper<MemoryModuleType<?>> MEMORY_MODULE_TYPE = getRegistryResolver(Registries.MEMORY_MODULE_TYPE);
    public static final KeyResolver.RegistryWrapper<MenuType<?>> MENU = getRegistryResolver(Registries.MENU);
    public static final KeyResolver.RegistryWrapper<MobEffect> MOB_EFFECT = getRegistryResolver(Registries.MOB_EFFECT);
    public static final KeyResolver.RegistryWrapper<PaintingVariant> PAINTING_VARIANT = getRegistryResolver(Registries.PAINTING_VARIANT);
    public static final KeyResolver.RegistryWrapper<ParticleType<?>> PARTICLE_TYPE = getRegistryResolver(Registries.PARTICLE_TYPE);
    public static final KeyResolver.RegistryWrapper<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = getRegistryResolver(Registries.PLACEMENT_MODIFIER_TYPE);
    public static final KeyResolver.RegistryWrapper<PoiType> POINT_OF_INTEREST_TYPE = getRegistryResolver(Registries.POINT_OF_INTEREST_TYPE);
    public static final KeyResolver.RegistryWrapper<PositionSourceType<?>> POSITION_SOURCE_TYPE = getRegistryResolver(Registries.POSITION_SOURCE_TYPE);
    public static final KeyResolver.RegistryWrapper<PosRuleTestType<?>> POS_RULE_TEST = getRegistryResolver(Registries.POS_RULE_TEST);
    public static final KeyResolver.RegistryWrapper<Potion> POTION = getRegistryResolver(Registries.POTION);
    public static final KeyResolver.RegistryWrapper<RecipeSerializer<?>> RECIPE_SERIALIZER = getRegistryResolver(Registries.RECIPE_SERIALIZER);
    public static final KeyResolver.RegistryWrapper<RecipeType<?>> RECIPE_TYPE = getRegistryResolver(Registries.RECIPE_TYPE);
    public static final KeyResolver.RegistryWrapper<RootPlacerType<?>> ROOT_PLACER_TYPE = getRegistryResolver(Registries.ROOT_PLACER_TYPE);
    public static final KeyResolver.RegistryWrapper<RuleTestType<?>> RULE_TEST = getRegistryResolver(Registries.RULE_TEST);
    public static final KeyResolver.RegistryWrapper<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = getRegistryResolver(Registries.RULE_BLOCK_ENTITY_MODIFIER);
    public static final KeyResolver.RegistryWrapper<Schedule> SCHEDULE = getRegistryResolver(Registries.SCHEDULE);
    public static final KeyResolver.RegistryWrapper<SensorType<?>> SENSOR_TYPE = getRegistryResolver(Registries.SENSOR_TYPE);
    public static final KeyResolver.RegistryWrapper<SoundEvent> SOUND_EVENT = getRegistryResolver(Registries.SOUND_EVENT);
    public static final KeyResolver.RegistryWrapper<StatType<?>> STAT_TYPE = getRegistryResolver(Registries.STAT_TYPE);
    public static final KeyResolver.RegistryWrapper<StructurePieceType> STRUCTURE_PIECE = getRegistryResolver(Registries.STRUCTURE_PIECE);
    public static final KeyResolver.RegistryWrapper<StructurePlacementType<?>> STRUCTURE_PLACEMENT = getRegistryResolver(Registries.STRUCTURE_PLACEMENT);
    public static final KeyResolver.RegistryWrapper<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = getRegistryResolver(Registries.STRUCTURE_POOL_ELEMENT);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends PoolAliasBinding>> POOL_ALIAS_BINDING = getRegistryResolver(Registries.POOL_ALIAS_BINDING);
    public static final KeyResolver.RegistryWrapper<StructureProcessorType<?>> STRUCTURE_PROCESSOR = getRegistryResolver(Registries.STRUCTURE_PROCESSOR);
    public static final KeyResolver.RegistryWrapper<StructureType<?>> STRUCTURE_TYPE = getRegistryResolver(Registries.STRUCTURE_TYPE);
    public static final KeyResolver.RegistryWrapper<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = getRegistryResolver(Registries.TREE_DECORATOR_TYPE);
    public static final KeyResolver.RegistryWrapper<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = getRegistryResolver(Registries.TRUNK_PLACER_TYPE);
    public static final KeyResolver.RegistryWrapper<VillagerProfession> VILLAGER_PROFESSION = getRegistryResolver(Registries.VILLAGER_PROFESSION);
    public static final KeyResolver.RegistryWrapper<VillagerType> VILLAGER_TYPE = getRegistryResolver(Registries.VILLAGER_TYPE);
    public static final KeyResolver.RegistryWrapper<String> DECORATED_POT_PATTERNS = getRegistryResolver(Registries.DECORATED_POT_PATTERNS);
    public static final KeyResolver.RegistryWrapper<NumberFormatType<?>> NUMBER_FORMAT_TYPE = getRegistryResolver(Registries.NUMBER_FORMAT_TYPE);
    public static final KeyResolver.RegistryWrapper<ArmorMaterial> ARMOR_MATERIAL = getRegistryResolver(Registries.ARMOR_MATERIAL);
    public static final KeyResolver.RegistryWrapper<DataComponentType<?>> DATA_COMPONENT_TYPE = getRegistryResolver(Registries.DATA_COMPONENT_TYPE);
    public static final KeyResolver.RegistryWrapper<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATE_TYPE = getRegistryResolver(Registries.ENTITY_SUB_PREDICATE_TYPE);
    public static final KeyResolver.RegistryWrapper<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPE = getRegistryResolver(Registries.ITEM_SUB_PREDICATE_TYPE);
    public static final KeyResolver.RegistryWrapper<MapDecorationType> MAP_DECORATION_TYPE = getRegistryResolver(Registries.MAP_DECORATION_TYPE);
//    public static final KeyResolver.RegistryWrapper<Biome> BIOME = getRegistryResolver(Registries.BIOME);
//    public static final KeyResolver.RegistryWrapper<ChatType> CHAT_TYPE = getRegistryResolver(Registries.CHAT_TYPE);
//    public static final KeyResolver.RegistryWrapper<ConfiguredWorldCarver<?>> CONFIGURED_CARVER = getRegistryResolver(Registries.CONFIGURED_CARVER);
//    public static final KeyResolver.RegistryWrapper<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = getRegistryResolver(Registries.CONFIGURED_FEATURE);
//    public static final KeyResolver.RegistryWrapper<DensityFunction> DENSITY_FUNCTION = getRegistryResolver(Registries.DENSITY_FUNCTION);
//    public static final KeyResolver.RegistryWrapper<DimensionType> DIMENSION_TYPE = getRegistryResolver(Registries.DIMENSION_TYPE);
//    public static final KeyResolver.RegistryWrapper<FlatLevelGeneratorPreset> FLAT_LEVEL_GENERATOR_PRESET = getRegistryResolver(Registries.FLAT_LEVEL_GENERATOR_PRESET);
//    public static final KeyResolver.RegistryWrapper<NoiseGeneratorSettings> NOISE_SETTINGS = getRegistryResolver(Registries.NOISE_SETTINGS);
//    public static final KeyResolver.RegistryWrapper<NormalNoise.NoiseParameters> NOISE = getRegistryResolver(Registries.NOISE);
//    public static final KeyResolver.RegistryWrapper<PlacedFeature> PLACED_FEATURE = getRegistryResolver(Registries.PLACED_FEATURE);
//    public static final KeyResolver.RegistryWrapper<Structure> STRUCTURE = getRegistryResolver(Registries.STRUCTURE);
//    public static final KeyResolver.RegistryWrapper<StructureProcessorList> PROCESSOR_LIST = getRegistryResolver(Registries.PROCESSOR_LIST);
//    public static final KeyResolver.RegistryWrapper<StructureSet> STRUCTURE_SET = getRegistryResolver(Registries.STRUCTURE_SET);
//    public static final KeyResolver.RegistryWrapper<StructureTemplatePool> TEMPLATE_POOL = getRegistryResolver(Registries.TEMPLATE_POOL);
    public static final KeyResolver.RegistryWrapper<CriterionTrigger<?>> TRIGGER_TYPE = getRegistryResolver(Registries.TRIGGER_TYPE);
//    public static final KeyResolver.RegistryWrapper<TrimMaterial> TRIM_MATERIAL = getRegistryResolver(Registries.TRIM_MATERIAL);
//    public static final KeyResolver.RegistryWrapper<TrimPattern> TRIM_PATTERN = getRegistryResolver(Registries.TRIM_PATTERN);
//    public static final KeyResolver.RegistryWrapper<WorldPreset> WORLD_PRESET = getRegistryResolver(Registries.WORLD_PRESET);
//    public static final KeyResolver.RegistryWrapper<MultiNoiseBiomeSourceParameterList> MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = getRegistryResolver(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST);
    public static final KeyResolver.RegistryWrapper<Level> DIMENSION = getRegistryResolver(Registries.DIMENSION);
    public static final KeyResolver.RegistryWrapper<LevelStem> LEVEL_STEM = getRegistryResolver(Registries.LEVEL_STEM);
    public static final KeyResolver.RegistryWrapper<LootTable> LOOT_TABLE = getRegistryResolver(Registries.LOOT_TABLE);
    public static final KeyResolver.RegistryWrapper<LootItemFunction> ITEM_MODIFIER = getRegistryResolver(Registries.ITEM_MODIFIER);
    public static final KeyResolver.RegistryWrapper<LootItemCondition> PREDICATE = getRegistryResolver(Registries.PREDICATE);

    @ApiStatus.Internal
    public static <T> void register(ResourceLocation key, KeyResolver<T> resolver) {
        if (RESOLVERS.putIfAbsent(key, resolver) != null) {
            throw new IllegalArgumentException("Mapping already registered for %s".formatted(key));
        }
    }

    @ApiStatus.Internal
    public static Iterable<Map.Entry<ResourceLocation, KeyResolver<?>>> getEntries() {
        return RESOLVERS.entrySet();
    }

    @Nullable
    public static <T> KeyResolver<T> getResolver(ResourceLocation key) {
        return (KeyResolver<T>) RESOLVERS.get(key);
    }

    @Nullable
    public static <T> KeyResolver.RegistryWrapper<T> getRegistryResolver(ResourceKey<? extends Registry<T>> key) {
        return (KeyResolver.RegistryWrapper<T>) RESOLVERS.get(key.location());
    }

    public static <T> ResourceLocation getId(KeyResolver<T> key) {
        return RESOLVERS.inverse().get(key);
    }
}
