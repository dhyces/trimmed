package dhyces.trimmed.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.Trimmed;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.NbtItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.TrimItemOverrideProvider;
import dhyces.trimmed.impl.client.override.ItemOverrideReloadListener;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ItemOverrideDataProvider implements DataProvider {

    protected final String modid;
    private final PackOutput dataOutput;
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

    protected void addItemOverrides(ItemLike item, ItemOverrideProvider... providers) {
        for (ItemOverrideProvider provider : providers) {
            providerMap.computeIfAbsent(item, itemConvertible -> new ArrayList<>()).add(provider);
        }
    }

    @Override
    public CompletableFuture<?> run(CachedOutput writer) {
        addItemOverrides();
        return CompletableFuture.allOf(providerMap.entrySet().stream().map((entry) -> {
            DataResult<JsonElement> encoded = ItemOverrideReloadListener.ITEM_OVERRIDE_CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            JsonElement json = encoded.getOrThrow(false, Trimmed.LOGGER::error);
            ResourceLocation id = BuiltInRegistries.ITEM.getKey(entry.getKey().asItem());
            return DataProvider.saveStable(writer, json, pathResolver.json(id));
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ItemOverrideProvider for " + modid;
    }
}
