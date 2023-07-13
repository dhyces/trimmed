package dhyces.trimmed.api.data;

import com.google.common.base.Suppliers;
import com.google.common.collect.ImmutableList;
import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.AnyTrimItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.NbtItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.TrimItemOverrideProvider;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.level.ItemLike;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public abstract class ItemOverrideDataProvider implements DataProvider {

    protected final String modid;
    protected final PackOutput dataOutput;
    protected final PackOutput.PathProvider pathResolver;
    private final Map<ItemLike, List<ItemOverrideProvider>> providerMap = new HashMap<>();

    public ItemOverrideDataProvider(PackOutput output, String modid) {
        this.dataOutput = output;
        this.pathResolver = output.createPathProvider(PackOutput.Target.RESOURCE_PACK, "models/item/overrides");
        this.modid = modid;
    }

    protected abstract void addItemOverrides();

    protected void addNbtOverride(ItemLike item, CompoundTag nbt, ResourceLocation itemModelId) {
        addItemOverrides(item, new NbtItemOverrideProvider(nbt, new ModelResourceLocation(itemModelId, "inventory")));
    }

    protected void addNbtOverride(ItemLike item, CompoundTag nbt, ModelResourceLocation modelId) {
        addItemOverrides(item, new NbtItemOverrideProvider(nbt, modelId));
    }

    protected void addTrimOverride(ItemLike item, ResourceKey<TrimMaterial> materialRegistryKey) {
        addTrimOverride(item, materialRegistryKey.location());
    }

    protected void addTrimOverride(ItemLike item, ResourceLocation materialId) {
        ResourceLocation itemId = BuiltInRegistries.ITEM.getKey(item.asItem());
        ModelResourceLocation itemModelId = new ModelResourceLocation(modid, "%s_%s_trim".formatted(itemId.getPath(), materialId.getPath()), "inventory");
        addTrimOverride(item, materialId, itemModelId);
    }

    protected void addTrimOverride(ItemLike item, ResourceLocation materialId, ResourceLocation itemModelId) {
        addTrimOverride(item, materialId, new ModelResourceLocation(itemModelId, "inventory"));
    }

    protected void addTrimOverride(ItemLike item, ResourceKey<TrimMaterial> materialRegistryKey, ModelResourceLocation modelId) {
        addTrimOverride(item, materialRegistryKey.location(), modelId);
    }

    protected void addTrimOverride(ItemLike item, ResourceLocation materialId, ModelResourceLocation modelId) {
        addItemOverrides(item, new TrimItemOverrideProvider(materialId, modelId));
    }

    protected ArmorSetTrimBuilder anyTrimBuilder(ArmorSet armorSet) {
        return new ArmorSetTrimBuilder(armorSet);
    }

    protected void addAnyTrimOverride(ItemLike item, ResourceLocation templateId, ResourceLocation trimTexture, List<ResourceLocation> excludedTextures) {
        addItemOverrides(item, new AnyTrimItemOverrideProvider(templateId, trimTexture, new LinkedHashSet<>(excludedTextures)));
    }

    protected void addItemOverrides(ItemLike item, ItemOverrideProvider... providers) {
        for (ItemOverrideProvider provider : providers) {
            providerMap.computeIfAbsent(item, itemConvertible -> new ArrayList<>()).add(provider);
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        addItemOverrides();
        return CompletableFuture.allOf(providerMap.entrySet().stream().map((entry) -> {
            DataResult<JsonElement> encoded = ItemOverrideProvider.LIST_CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            JsonElement json = encoded.getOrThrow(false, Trimmed.LOGGER::error);
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(entry.getKey().asItem());
            return DataProvider.saveStable(writer, json, pathResolver.json(id));
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ItemOverrideProvider for " + modid;
    }

    public record ArmorSet(ItemLike helmet, ItemLike chestplate, ItemLike leggings, ItemLike boots) {}

    public class ArmorSetTrimBuilder {
        private ArmorSet armorSet;
        private ResourceLocation templateId;
        private final Set<ResourceLocation> excludedTextures = new LinkedHashSet<>();

        public static final Supplier<List<ResourceLocation>> VANILLA_DARKER_COLORS = Suppliers.memoize(() -> {
            ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
            builder.add(new ResourceLocation("minecraft:trims/color_palettes/iron_darker"));
            builder.add(new ResourceLocation("minecraft:trims/color_palettes/gold_darker"));
            builder.add(new ResourceLocation("minecraft:trims/color_palettes/diamond_darker"));
            builder.add(new ResourceLocation("minecraft:trims/color_palettes/netherite_darker"));
            return builder.build();
        });

        ArmorSetTrimBuilder(ArmorSet armorSet) {
            this.armorSet = armorSet;
        }

        public ArmorSetTrimBuilder twoLayer() {
            templateId = Trimmed.id("item/two_layer_trim");
            return this;
        }

        public ArmorSetTrimBuilder threeLayer() {
            templateId = Trimmed.id("item/three_layer_trim");
            return this;
        }

        public ArmorSetTrimBuilder setTemplateId(ResourceLocation templateId) {
            this.templateId = templateId;
            return this;
        }

        public ArmorSetTrimBuilder excludeVanillaDarker() {
            this.excludedTextures.add(new ResourceLocation("minecraft:trims/color_palettes/iron_darker"));
            this.excludedTextures.add(new ResourceLocation("minecraft:trims/color_palettes/gold_darker"));
            this.excludedTextures.add(new ResourceLocation("minecraft:trims/color_palettes/diamond_darker"));
            this.excludedTextures.add(new ResourceLocation("minecraft:trims/color_palettes/netherite_darker"));
            return this;
        }

        public ArmorSetTrimBuilder exclude(ResourceLocation resourceLocation) {
            this.excludedTextures.add(resourceLocation);
            return this;
        }

        public ArmorSetTrimBuilder exclude(ResourceLocation... resourceLocations) {
            this.excludedTextures.addAll(Arrays.asList(resourceLocations));
            return this;
        }

        public ArmorSetTrimBuilder include(ResourceLocation resourceLocation) {
            this.excludedTextures.remove(resourceLocation);
            return this;
        }

        public void end() {
            Objects.requireNonNull(templateId);

            addItemOverrides(armorSet.helmet, new AnyTrimItemOverrideProvider(templateId, new ResourceLocation("minecraft:trims/items/helmet_trim"), excludedTextures));
            addItemOverrides(armorSet.chestplate, new AnyTrimItemOverrideProvider(templateId, new ResourceLocation("minecraft:trims/items/chestplate_trim"), excludedTextures));
            addItemOverrides(armorSet.leggings, new AnyTrimItemOverrideProvider(templateId, new ResourceLocation("minecraft:trims/items/leggings_trim"), excludedTextures));
            addItemOverrides(armorSet.boots, new AnyTrimItemOverrideProvider(templateId, new ResourceLocation("minecraft:trims/items/boots_trim"), excludedTextures));
        }
    }
}
