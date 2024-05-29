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
            RESOLVERS.put(registry.key().location(), new KeyResolver.RegistryWrapper<>(registry, false));
        }
    }

    public static final KeyResolver<Activity> ACTIVITY = getResolver(Registries.ACTIVITY.location());
    public static final KeyResolver<Attribute> ATTRIBUTE = getResolver(Registries.ATTRIBUTE.location());
//    public static final KeyResolver<BannerPattern> BANNER_PATTERN = getResolver(Registries.BANNER_PATTERN.location());
    public static final KeyResolver<MapCodec<? extends BiomeSource>> BIOME_SOURCE = getResolver(Registries.BIOME_SOURCE.location());
    public static final KeyResolver<Block> BLOCK = getResolver(Registries.BLOCK.location());
    public static final KeyResolver<MapCodec<? extends Block>> BLOCK_TYPE = getResolver(Registries.BLOCK_TYPE.location());
    public static final KeyResolver<BlockEntityType<?>> BLOCK_ENTITY_TYPE = getResolver(Registries.BLOCK_ENTITY_TYPE.location());
    public static final KeyResolver<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = getResolver(Registries.BLOCK_PREDICATE_TYPE.location());
    public static final KeyResolver<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = getResolver(Registries.BLOCK_STATE_PROVIDER_TYPE.location());
    public static final KeyResolver<WorldCarver<?>> CARVER = getResolver(Registries.CARVER.location());
    public static final KeyResolver<CatVariant> CAT_VARIANT = getResolver(Registries.CAT_VARIANT.location());
//    public static final KeyResolver<WolfVariant> WOLF_VARIANT = getResolver(Registries.WOLF_VARIANT.location());
    public static final KeyResolver<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR = getResolver(Registries.CHUNK_GENERATOR.location());
    public static final KeyResolver<ChunkStatus> CHUNK_STATUS = getResolver(Registries.CHUNK_STATUS.location());
    public static final KeyResolver<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = getResolver(Registries.COMMAND_ARGUMENT_TYPE.location());
    public static final KeyResolver<CreativeModeTab> CREATIVE_MODE_TAB = getResolver(Registries.CREATIVE_MODE_TAB.location());
    public static final KeyResolver<ResourceLocation> CUSTOM_STAT = getResolver(Registries.CUSTOM_STAT.location());
//    public static final KeyResolver<DamageType> DAMAGE_TYPE = getResolver(Registries.DAMAGE_TYPE.location());
    public static final KeyResolver<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = getResolver(Registries.DENSITY_FUNCTION_TYPE.location());
    public static final KeyResolver<Enchantment> ENCHANTMENT = getResolver(Registries.ENCHANTMENT.location());
    public static final KeyResolver<EntityType<?>> ENTITY_TYPE = getResolver(Registries.ENTITY_TYPE.location());
    public static final KeyResolver<Feature<?>> FEATURE = getResolver(Registries.FEATURE.location());
    public static final KeyResolver<FeatureSizeType<?>> FEATURE_SIZE_TYPE = getResolver(Registries.FEATURE_SIZE_TYPE.location());
    public static final KeyResolver<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = getResolver(Registries.FLOAT_PROVIDER_TYPE.location());
    public static final KeyResolver<Fluid> FLUID = getResolver(Registries.FLUID.location());
    public static final KeyResolver<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = getResolver(Registries.FOLIAGE_PLACER_TYPE.location());
    public static final KeyResolver<FrogVariant> FROG_VARIANT = getResolver(Registries.FROG_VARIANT.location());
    public static final KeyResolver<GameEvent> GAME_EVENT = getResolver(Registries.GAME_EVENT.location());
    public static final KeyResolver<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = getResolver(Registries.HEIGHT_PROVIDER_TYPE.location());
    public static final KeyResolver<Instrument> INSTRUMENT = getResolver(Registries.INSTRUMENT.location());
    public static final KeyResolver<IntProviderType<?>> INT_PROVIDER_TYPE = getResolver(Registries.INT_PROVIDER_TYPE.location());
    public static final KeyResolver<Item> ITEM = getResolver(Registries.ITEM.location());
    public static final KeyResolver<LootItemConditionType> LOOT_CONDITION_TYPE = getResolver(Registries.LOOT_CONDITION_TYPE.location());
    public static final KeyResolver<LootItemFunctionType<?>> LOOT_FUNCTION_TYPE = getResolver(Registries.LOOT_FUNCTION_TYPE.location());
    public static final KeyResolver<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = getResolver(Registries.LOOT_NBT_PROVIDER_TYPE.location());
    public static final KeyResolver<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = getResolver(Registries.LOOT_NUMBER_PROVIDER_TYPE.location());
    public static final KeyResolver<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = getResolver(Registries.LOOT_POOL_ENTRY_TYPE.location());
    public static final KeyResolver<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = getResolver(Registries.LOOT_SCORE_PROVIDER_TYPE.location());
    public static final KeyResolver<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = getResolver(Registries.MATERIAL_CONDITION.location());
    public static final KeyResolver<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = getResolver(Registries.MATERIAL_RULE.location());
    public static final KeyResolver<MemoryModuleType<?>> MEMORY_MODULE_TYPE = getResolver(Registries.MEMORY_MODULE_TYPE.location());
    public static final KeyResolver<MenuType<?>> MENU = getResolver(Registries.MENU.location());
    public static final KeyResolver<MobEffect> MOB_EFFECT = getResolver(Registries.MOB_EFFECT.location());
    public static final KeyResolver<PaintingVariant> PAINTING_VARIANT = getResolver(Registries.PAINTING_VARIANT.location());
    public static final KeyResolver<ParticleType<?>> PARTICLE_TYPE = getResolver(Registries.PARTICLE_TYPE.location());
    public static final KeyResolver<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = getResolver(Registries.PLACEMENT_MODIFIER_TYPE.location());
    public static final KeyResolver<PoiType> POINT_OF_INTEREST_TYPE = getResolver(Registries.POINT_OF_INTEREST_TYPE.location());
    public static final KeyResolver<PositionSourceType<?>> POSITION_SOURCE_TYPE = getResolver(Registries.POSITION_SOURCE_TYPE.location());
    public static final KeyResolver<PosRuleTestType<?>> POS_RULE_TEST = getResolver(Registries.POS_RULE_TEST.location());
    public static final KeyResolver<Potion> POTION = getResolver(Registries.POTION.location());
    public static final KeyResolver<RecipeSerializer<?>> RECIPE_SERIALIZER = getResolver(Registries.RECIPE_SERIALIZER.location());
    public static final KeyResolver<RecipeType<?>> RECIPE_TYPE = getResolver(Registries.RECIPE_TYPE.location());
    public static final KeyResolver<RootPlacerType<?>> ROOT_PLACER_TYPE = getResolver(Registries.ROOT_PLACER_TYPE.location());
    public static final KeyResolver<RuleTestType<?>> RULE_TEST = getResolver(Registries.RULE_TEST.location());
    public static final KeyResolver<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = getResolver(Registries.RULE_BLOCK_ENTITY_MODIFIER.location());
    public static final KeyResolver<Schedule> SCHEDULE = getResolver(Registries.SCHEDULE.location());
    public static final KeyResolver<SensorType<?>> SENSOR_TYPE = getResolver(Registries.SENSOR_TYPE.location());
    public static final KeyResolver<SoundEvent> SOUND_EVENT = getResolver(Registries.SOUND_EVENT.location());
    public static final KeyResolver<StatType<?>> STAT_TYPE = getResolver(Registries.STAT_TYPE.location());
    public static final KeyResolver<StructurePieceType> STRUCTURE_PIECE = getResolver(Registries.STRUCTURE_PIECE.location());
    public static final KeyResolver<StructurePlacementType<?>> STRUCTURE_PLACEMENT = getResolver(Registries.STRUCTURE_PLACEMENT.location());
    public static final KeyResolver<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = getResolver(Registries.STRUCTURE_POOL_ELEMENT.location());
    public static final KeyResolver<MapCodec<? extends PoolAliasBinding>> POOL_ALIAS_BINDING = getResolver(Registries.POOL_ALIAS_BINDING.location());
    public static final KeyResolver<StructureProcessorType<?>> STRUCTURE_PROCESSOR = getResolver(Registries.STRUCTURE_PROCESSOR.location());
    public static final KeyResolver<StructureType<?>> STRUCTURE_TYPE = getResolver(Registries.STRUCTURE_TYPE.location());
    public static final KeyResolver<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = getResolver(Registries.TREE_DECORATOR_TYPE.location());
    public static final KeyResolver<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = getResolver(Registries.TRUNK_PLACER_TYPE.location());
    public static final KeyResolver<VillagerProfession> VILLAGER_PROFESSION = getResolver(Registries.VILLAGER_PROFESSION.location());
    public static final KeyResolver<VillagerType> VILLAGER_TYPE = getResolver(Registries.VILLAGER_TYPE.location());
    public static final KeyResolver<String> DECORATED_POT_PATTERNS = getResolver(Registries.DECORATED_POT_PATTERNS.location());
    public static final KeyResolver<NumberFormatType<?>> NUMBER_FORMAT_TYPE = getResolver(Registries.NUMBER_FORMAT_TYPE.location());
    public static final KeyResolver<ArmorMaterial> ARMOR_MATERIAL = getResolver(Registries.ARMOR_MATERIAL.location());
    public static final KeyResolver<DataComponentType<?>> DATA_COMPONENT_TYPE = getResolver(Registries.DATA_COMPONENT_TYPE.location());
    public static final KeyResolver<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATE_TYPE = getResolver(Registries.ENTITY_SUB_PREDICATE_TYPE.location());
    public static final KeyResolver<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPE = getResolver(Registries.ITEM_SUB_PREDICATE_TYPE.location());
    public static final KeyResolver<MapDecorationType> MAP_DECORATION_TYPE = getResolver(Registries.MAP_DECORATION_TYPE.location());
//    public static final KeyResolver<Biome> BIOME = getResolver(Registries.BIOME.location());
//    public static final KeyResolver<ChatType> CHAT_TYPE = getResolver(Registries.CHAT_TYPE.location());
//    public static final KeyResolver<ConfiguredWorldCarver<?>> CONFIGURED_CARVER = getResolver(Registries.CONFIGURED_CARVER.location());
//    public static final KeyResolver<ConfiguredFeature<?, ?>> CONFIGURED_FEATURE = getResolver(Registries.CONFIGURED_FEATURE.location());
//    public static final KeyResolver<DensityFunction> DENSITY_FUNCTION = getResolver(Registries.DENSITY_FUNCTION.location());
//    public static final KeyResolver<DimensionType> DIMENSION_TYPE = getResolver(Registries.DIMENSION_TYPE.location());
//    public static final KeyResolver<FlatLevelGeneratorPreset> FLAT_LEVEL_GENERATOR_PRESET = getResolver(Registries.FLAT_LEVEL_GENERATOR_PRESET.location());
//    public static final KeyResolver<NoiseGeneratorSettings> NOISE_SETTINGS = getResolver(Registries.NOISE_SETTINGS.location());
//    public static final KeyResolver<NormalNoise.NoiseParameters> NOISE = getResolver(Registries.NOISE.location());
//    public static final KeyResolver<PlacedFeature> PLACED_FEATURE = getResolver(Registries.PLACED_FEATURE.location());
//    public static final KeyResolver<Structure> STRUCTURE = getResolver(Registries.STRUCTURE.location());
//    public static final KeyResolver<StructureProcessorList> PROCESSOR_LIST = getResolver(Registries.PROCESSOR_LIST.location());
//    public static final KeyResolver<StructureSet> STRUCTURE_SET = getResolver(Registries.STRUCTURE_SET.location());
//    public static final KeyResolver<StructureTemplatePool> TEMPLATE_POOL = getResolver(Registries.TEMPLATE_POOL.location());
    public static final KeyResolver<CriterionTrigger<?>> TRIGGER_TYPE = getResolver(Registries.TRIGGER_TYPE.location());
//    public static final KeyResolver<TrimMaterial> TRIM_MATERIAL = getResolver(Registries.TRIM_MATERIAL.location());
//    public static final KeyResolver<TrimPattern> TRIM_PATTERN = getResolver(Registries.TRIM_PATTERN.location());
//    public static final KeyResolver<WorldPreset> WORLD_PRESET = getResolver(Registries.WORLD_PRESET.location());
//    public static final KeyResolver<MultiNoiseBiomeSourceParameterList> MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST = getResolver(Registries.MULTI_NOISE_BIOME_SOURCE_PARAMETER_LIST.location());
    public static final KeyResolver<Level> DIMENSION = getResolver(Registries.DIMENSION.location());
    public static final KeyResolver<LevelStem> LEVEL_STEM = getResolver(Registries.LEVEL_STEM.location());
    public static final KeyResolver<LootTable> LOOT_TABLE = getResolver(Registries.LOOT_TABLE.location());
    public static final KeyResolver<LootItemFunction> ITEM_MODIFIER = getResolver(Registries.ITEM_MODIFIER.location());
    public static final KeyResolver<LootItemCondition> PREDICATE = getResolver(Registries.PREDICATE.location());

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

    public static <T> ResourceLocation getId(KeyResolver<T> key) {
        return RESOLVERS.inverse().get(key);
    }
}
