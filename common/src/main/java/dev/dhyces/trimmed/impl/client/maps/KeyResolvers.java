package dev.dhyces.trimmed.impl.client.maps;

import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.KeyResolver;
import net.minecraft.advancements.CriterionTrigger;
import net.minecraft.advancements.critereon.EntitySubPredicate;
import net.minecraft.advancements.critereon.ItemSubPredicate;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.particles.ParticleType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.ChatType;
import net.minecraft.network.chat.numbers.NumberFormatType;
import net.minecraft.resources.RegistryFileCodec;
import net.minecraft.resources.RegistryFixedCodec;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.stats.StatType;
import net.minecraft.util.valueproviders.FloatProviderType;
import net.minecraft.util.valueproviders.IntProviderType;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.memory.MemoryModuleType;
import net.minecraft.world.entity.ai.sensing.SensorType;
import net.minecraft.world.entity.ai.village.poi.PoiType;
import net.minecraft.world.entity.animal.CatVariant;
import net.minecraft.world.entity.animal.FrogVariant;
import net.minecraft.world.entity.animal.WolfVariant;
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
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BannerPattern;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.entity.DecoratedPotPattern;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.chunk.status.ChunkStatus;
import net.minecraft.world.level.dimension.DimensionType;
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

    public static final KeyResolver.Static<Activity> ACTIVITY = registerStaticRegistry(BuiltInRegistries.ACTIVITY);
    public static final KeyResolver.Static<Attribute> ATTRIBUTE = registerStaticRegistry(BuiltInRegistries.ATTRIBUTE);
    public static final KeyResolver.Dynamic<BannerPattern> BANNER_PATTERN = registerDynamicRegistry(Registries.BANNER_PATTERN);
    public static final KeyResolver.Static<MapCodec<? extends BiomeSource>> BIOME_SOURCE = registerStaticRegistry(BuiltInRegistries.BIOME_SOURCE);
    public static final KeyResolver.Static<Block> BLOCK = registerStaticRegistry(BuiltInRegistries.BLOCK);
    public static final KeyResolver.Static<MapCodec<? extends Block>> BLOCK_TYPE = registerStaticRegistry(BuiltInRegistries.BLOCK_TYPE);
    public static final KeyResolver.Static<BlockEntityType<?>> BLOCK_ENTITY_TYPE = registerStaticRegistry(BuiltInRegistries.BLOCK_ENTITY_TYPE);
    public static final KeyResolver.Static<BlockPredicateType<?>> BLOCK_PREDICATE_TYPE = registerStaticRegistry(BuiltInRegistries.BLOCK_PREDICATE_TYPE);
    public static final KeyResolver.Static<BlockStateProviderType<?>> BLOCK_STATE_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.BLOCKSTATE_PROVIDER_TYPE);
    public static final KeyResolver.Static<WorldCarver<?>> CARVER = registerStaticRegistry(BuiltInRegistries.CARVER);
    public static final KeyResolver.Static<CatVariant> CAT_VARIANT = registerStaticRegistry(BuiltInRegistries.CAT_VARIANT);
    public static final KeyResolver.Dynamic<WolfVariant> WOLF_VARIANT = registerDynamicRegistry(Registries.WOLF_VARIANT);
    public static final KeyResolver.Static<MapCodec<? extends ChunkGenerator>> CHUNK_GENERATOR = registerStaticRegistry(BuiltInRegistries.CHUNK_GENERATOR);
    public static final KeyResolver.Static<ChunkStatus> CHUNK_STATUS = registerStaticRegistry(BuiltInRegistries.CHUNK_STATUS);
    public static final KeyResolver.Static<ArgumentTypeInfo<?, ?>> COMMAND_ARGUMENT_TYPE = registerStaticRegistry(BuiltInRegistries.COMMAND_ARGUMENT_TYPE);
    public static final KeyResolver.Static<CreativeModeTab> CREATIVE_MODE_TAB = registerStaticRegistry(BuiltInRegistries.CREATIVE_MODE_TAB);
    public static final KeyResolver.Static<ResourceLocation> CUSTOM_STAT = registerStaticRegistry(BuiltInRegistries.CUSTOM_STAT);
    public static final KeyResolver.Dynamic<DamageType> DAMAGE_TYPE = registerDynamicRegistry(Registries.DAMAGE_TYPE);
    public static final KeyResolver.Static<MapCodec<? extends DensityFunction>> DENSITY_FUNCTION_TYPE = registerStaticRegistry(BuiltInRegistries.DENSITY_FUNCTION_TYPE);
    public static final KeyResolver.Dynamic<Enchantment> ENCHANTMENT = registerDynamicRegistry(Registries.ENCHANTMENT);
    public static final KeyResolver.Static<EntityType<?>> ENTITY_TYPE = registerStaticRegistry(BuiltInRegistries.ENTITY_TYPE);
    public static final KeyResolver.Static<Feature<?>> FEATURE = registerStaticRegistry(BuiltInRegistries.FEATURE);
    public static final KeyResolver.Static<FeatureSizeType<?>> FEATURE_SIZE_TYPE = registerStaticRegistry(BuiltInRegistries.FEATURE_SIZE_TYPE);
    public static final KeyResolver.Static<FloatProviderType<?>> FLOAT_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.FLOAT_PROVIDER_TYPE);
    public static final KeyResolver.Static<Fluid> FLUID = registerStaticRegistry(BuiltInRegistries.FLUID);
    public static final KeyResolver.Static<FoliagePlacerType<?>> FOLIAGE_PLACER_TYPE = registerStaticRegistry(BuiltInRegistries.FOLIAGE_PLACER_TYPE);
    public static final KeyResolver.Static<FrogVariant> FROG_VARIANT = registerStaticRegistry(BuiltInRegistries.FROG_VARIANT);
    public static final KeyResolver.Static<GameEvent> GAME_EVENT = registerStaticRegistry(BuiltInRegistries.GAME_EVENT);
    public static final KeyResolver.Static<HeightProviderType<?>> HEIGHT_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.HEIGHT_PROVIDER_TYPE);
    public static final KeyResolver.Static<Instrument> INSTRUMENT = registerStaticRegistry(BuiltInRegistries.INSTRUMENT);
    public static final KeyResolver.Static<IntProviderType<?>> INT_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.INT_PROVIDER_TYPE);
    public static final KeyResolver.Static<Item> ITEM = registerStaticRegistry(BuiltInRegistries.ITEM);
    public static final KeyResolver.Static<LootItemConditionType> LOOT_CONDITION_TYPE = registerStaticRegistry(BuiltInRegistries.LOOT_CONDITION_TYPE);
    public static final KeyResolver.Static<LootItemFunctionType<?>> LOOT_FUNCTION_TYPE = registerStaticRegistry(BuiltInRegistries.LOOT_FUNCTION_TYPE);
    public static final KeyResolver.Static<LootNbtProviderType> LOOT_NBT_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.LOOT_NBT_PROVIDER_TYPE);
    public static final KeyResolver.Static<LootNumberProviderType> LOOT_NUMBER_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.LOOT_NUMBER_PROVIDER_TYPE);
    public static final KeyResolver.Static<LootPoolEntryType> LOOT_POOL_ENTRY_TYPE = registerStaticRegistry(BuiltInRegistries.LOOT_POOL_ENTRY_TYPE);
    public static final KeyResolver.Static<LootScoreProviderType> LOOT_SCORE_PROVIDER_TYPE = registerStaticRegistry(BuiltInRegistries.LOOT_SCORE_PROVIDER_TYPE);
    public static final KeyResolver.Static<MapCodec<? extends SurfaceRules.ConditionSource>> MATERIAL_CONDITION = registerStaticRegistry(BuiltInRegistries.MATERIAL_CONDITION);
    public static final KeyResolver.Static<MapCodec<? extends SurfaceRules.RuleSource>> MATERIAL_RULE = registerStaticRegistry(BuiltInRegistries.MATERIAL_RULE);
    public static final KeyResolver.Static<MemoryModuleType<?>> MEMORY_MODULE_TYPE = registerStaticRegistry(BuiltInRegistries.MEMORY_MODULE_TYPE);
    public static final KeyResolver.Static<MenuType<?>> MENU = registerStaticRegistry(BuiltInRegistries.MENU);
    public static final KeyResolver.Static<MobEffect> MOB_EFFECT = registerStaticRegistry(BuiltInRegistries.MOB_EFFECT);
    public static final KeyResolver.Dynamic<PaintingVariant> PAINTING_VARIANT = registerDynamicRegistry(Registries.PAINTING_VARIANT);
    public static final KeyResolver.Static<ParticleType<?>> PARTICLE_TYPE = registerStaticRegistry(BuiltInRegistries.PARTICLE_TYPE);
    public static final KeyResolver.Static<PlacementModifierType<?>> PLACEMENT_MODIFIER_TYPE = registerStaticRegistry(BuiltInRegistries.PLACEMENT_MODIFIER_TYPE);
    public static final KeyResolver.Static<PoiType> POINT_OF_INTEREST_TYPE = registerStaticRegistry(BuiltInRegistries.POINT_OF_INTEREST_TYPE);
    public static final KeyResolver.Static<PositionSourceType<?>> POSITION_SOURCE_TYPE = registerStaticRegistry(BuiltInRegistries.POSITION_SOURCE_TYPE);
    public static final KeyResolver.Static<PosRuleTestType<?>> POS_RULE_TEST = registerStaticRegistry(BuiltInRegistries.POS_RULE_TEST);
    public static final KeyResolver.Static<Potion> POTION = registerStaticRegistry(BuiltInRegistries.POTION);
    public static final KeyResolver.Static<RecipeSerializer<?>> RECIPE_SERIALIZER = registerStaticRegistry(BuiltInRegistries.RECIPE_SERIALIZER);
    public static final KeyResolver.Static<RecipeType<?>> RECIPE_TYPE = registerStaticRegistry(BuiltInRegistries.RECIPE_TYPE);
    public static final KeyResolver.Static<RootPlacerType<?>> ROOT_PLACER_TYPE = registerStaticRegistry(BuiltInRegistries.ROOT_PLACER_TYPE);
    public static final KeyResolver.Static<RuleTestType<?>> RULE_TEST = registerStaticRegistry(BuiltInRegistries.RULE_TEST);
    public static final KeyResolver.Static<RuleBlockEntityModifierType<?>> RULE_BLOCK_ENTITY_MODIFIER = registerStaticRegistry(BuiltInRegistries.RULE_BLOCK_ENTITY_MODIFIER);
    public static final KeyResolver.Static<Schedule> SCHEDULE = registerStaticRegistry(BuiltInRegistries.SCHEDULE);
    public static final KeyResolver.Static<SensorType<?>> SENSOR_TYPE = registerStaticRegistry(BuiltInRegistries.SENSOR_TYPE);
    public static final KeyResolver.Static<SoundEvent> SOUND_EVENT = registerStaticRegistry(BuiltInRegistries.SOUND_EVENT);
    public static final KeyResolver.Static<StatType<?>> STAT_TYPE = registerStaticRegistry(BuiltInRegistries.STAT_TYPE);
    public static final KeyResolver.Static<StructurePieceType> STRUCTURE_PIECE = registerStaticRegistry(BuiltInRegistries.STRUCTURE_PIECE);
    public static final KeyResolver.Static<StructurePlacementType<?>> STRUCTURE_PLACEMENT = registerStaticRegistry(BuiltInRegistries.STRUCTURE_PLACEMENT);
    public static final KeyResolver.Static<StructurePoolElementType<?>> STRUCTURE_POOL_ELEMENT = registerStaticRegistry(BuiltInRegistries.STRUCTURE_POOL_ELEMENT);
    public static final KeyResolver.Static<MapCodec<? extends PoolAliasBinding>> POOL_ALIAS_BINDING = registerStaticRegistry(BuiltInRegistries.POOL_ALIAS_BINDING_TYPE);
    public static final KeyResolver.Static<StructureProcessorType<?>> STRUCTURE_PROCESSOR = registerStaticRegistry(BuiltInRegistries.STRUCTURE_PROCESSOR);
    public static final KeyResolver.Static<StructureType<?>> STRUCTURE_TYPE = registerStaticRegistry(BuiltInRegistries.STRUCTURE_TYPE);
    public static final KeyResolver.Static<TreeDecoratorType<?>> TREE_DECORATOR_TYPE = registerStaticRegistry(BuiltInRegistries.TREE_DECORATOR_TYPE);
    public static final KeyResolver.Static<TrunkPlacerType<?>> TRUNK_PLACER_TYPE = registerStaticRegistry(BuiltInRegistries.TRUNK_PLACER_TYPE);
    public static final KeyResolver.Static<VillagerProfession> VILLAGER_PROFESSION = registerStaticRegistry(BuiltInRegistries.VILLAGER_PROFESSION);
    public static final KeyResolver.Static<VillagerType> VILLAGER_TYPE = registerStaticRegistry(BuiltInRegistries.VILLAGER_TYPE);
    public static final KeyResolver.Static<DecoratedPotPattern> DECORATED_POT_PATTERNS = registerStaticRegistry(BuiltInRegistries.DECORATED_POT_PATTERN);
    public static final KeyResolver.Static<NumberFormatType<?>> NUMBER_FORMAT_TYPE = registerStaticRegistry(BuiltInRegistries.NUMBER_FORMAT_TYPE);
    public static final KeyResolver.Static<ArmorMaterial> ARMOR_MATERIAL = registerStaticRegistry(BuiltInRegistries.ARMOR_MATERIAL);
    public static final KeyResolver.Static<DataComponentType<?>> DATA_COMPONENT_TYPE = registerStaticRegistry(BuiltInRegistries.DATA_COMPONENT_TYPE);
    public static final KeyResolver.Static<MapCodec<? extends EntitySubPredicate>> ENTITY_SUB_PREDICATE_TYPE = registerStaticRegistry(BuiltInRegistries.ENTITY_SUB_PREDICATE_TYPE);
    public static final KeyResolver.Static<ItemSubPredicate.Type<?>> ITEM_SUB_PREDICATE_TYPE = registerStaticRegistry(BuiltInRegistries.ITEM_SUB_PREDICATE_TYPE);
    public static final KeyResolver.Static<MapDecorationType> MAP_DECORATION_TYPE = registerStaticRegistry(BuiltInRegistries.MAP_DECORATION_TYPE);
    public static final KeyResolver.Dynamic<Biome> BIOME = registerDynamicRegistry(Registries.BIOME);
    public static final KeyResolver.Dynamic<ChatType> CHAT_TYPE = registerDynamicRegistry(Registries.CHAT_TYPE);
    public static final KeyResolver.Dynamic<DimensionType> DIMENSION_TYPE = registerDynamicRegistry(Registries.DIMENSION_TYPE);
    public static final KeyResolver.Static<CriterionTrigger<?>> TRIGGER_TYPE = registerStaticRegistry(BuiltInRegistries.TRIGGER_TYPES);
    public static final KeyResolver.Dynamic<TrimMaterial> TRIM_MATERIAL = registerDynamicRegistry(Registries.TRIM_MATERIAL);
    public static final KeyResolver.Dynamic<TrimPattern> TRIM_PATTERN = registerDynamicRegistry(Registries.TRIM_PATTERN);

    @ApiStatus.Internal
    public static <T> void register(ResourceLocation key, KeyResolver<T> resolver) {
        if (RESOLVERS.putIfAbsent(key, resolver) != null) {
            throw new IllegalArgumentException("Mapping already registered for %s".formatted(key));
        }
    }

    private static <T> KeyResolver.Static<T> registerStaticRegistry(final Registry<T> registry) {
        var resolver = new KeyResolver.Static<>(registry);
        register(registry.key().location(), resolver);
        return resolver;
    }

    private static <T> KeyResolver.Dynamic<T> registerDynamicRegistry(final ResourceKey<? extends Registry<T>> registryKey) {
        var resolver = new KeyResolver.Dynamic<>(registryKey);
        register(registryKey.location(), resolver);
        return resolver;
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
    public static <T> KeyResolver.RegistryResolver<T> getRegistryResolver(ResourceKey<? extends Registry<T>> key) {
        return (KeyResolver.RegistryResolver<T>) RESOLVERS.get(key.location());
    }

    @Nullable
    public static <T> ResourceLocation getId(KeyResolver<T> key) {
        return RESOLVERS.inverse().get(key);
    }
}
