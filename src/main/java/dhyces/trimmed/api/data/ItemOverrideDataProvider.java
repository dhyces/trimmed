package dhyces.trimmed.api.data;

import com.google.gson.JsonElement;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.JsonOps;
import dhyces.trimmed.TrimmedClient;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.NbtItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.providers.TrimItemOverrideProvider;
import dhyces.trimmed.impl.client.override.ItemOverrideReloadListener;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class ItemOverrideDataProvider implements DataProvider {

    private final FabricDataOutput dataOutput;
    protected final DataOutput.PathResolver pathResolver;
    private final Map<ItemConvertible, List<ItemOverrideProvider>> providerMap = new HashMap<>();

    public ItemOverrideDataProvider(FabricDataOutput output) {
        this.dataOutput = output;
        this.pathResolver = output.getResolver(DataOutput.OutputType.RESOURCE_PACK, "models/item/overrides");
    }

    protected abstract void addItemOverrides();

    protected void addNbtOverride(ItemConvertible item, NbtCompound nbt, Identifier itemModelId) {
        addItemOverrides(item, new NbtItemOverrideProvider(nbt, new ModelIdentifier(itemModelId, "inventory")));
    }

    protected void addNbtOverride(ItemConvertible item, NbtCompound nbt, ModelIdentifier modelId) {
        addItemOverrides(item, new NbtItemOverrideProvider(nbt, modelId));
    }

    protected void addTrimOverride(ItemConvertible item, RegistryKey<ArmorTrimMaterial> materialRegistryKey) {
        addTrimOverride(item, materialRegistryKey.getValue());
    }

    protected void addTrimOverride(ItemConvertible item, Identifier materialId) {
        Identifier itemId = Registries.ITEM.getId(item.asItem());
        ModelIdentifier itemModelId = new ModelIdentifier(dataOutput.getModId(), "%s_%s_trim".formatted(itemId.getPath(), materialId.getPath()), "inventory");
        addTrimOverride(item, materialId, itemModelId);
    }

    protected void addTrimOverride(ItemConvertible item, Identifier materialId, Identifier itemModelId) {
        addTrimOverride(item, materialId, new ModelIdentifier(itemModelId, "inventory"));
    }

    protected void addTrimOverride(ItemConvertible item, RegistryKey<ArmorTrimMaterial> materialRegistryKey, ModelIdentifier modelId) {
        addTrimOverride(item, materialRegistryKey.getValue(), modelId);
    }

    protected void addTrimOverride(ItemConvertible item, Identifier materialId, ModelIdentifier modelId) {
        addItemOverrides(item, new TrimItemOverrideProvider(materialId, modelId));
    }

    protected void addItemOverrides(ItemConvertible item, ItemOverrideProvider... providers) {
        for (ItemOverrideProvider provider : providers) {
            providerMap.computeIfAbsent(item, itemConvertible -> new ArrayList<>()).add(provider);
        }
    }

    @Override
    public CompletableFuture<?> run(DataWriter writer) {
        addItemOverrides();
        return CompletableFuture.allOf(providerMap.entrySet().stream().map((entry) -> {
            DataResult<JsonElement> encoded = ItemOverrideReloadListener.ITEM_OVERRIDE_CODEC.encodeStart(JsonOps.INSTANCE, entry.getValue());
            JsonElement json = encoded.getOrThrow(false, TrimmedClient.LOGGER::error);
            Identifier id = Registries.ITEM.getId(entry.getKey().asItem());
            return DataProvider.writeToPath(writer, json, pathResolver.resolveJson(id));
        }).toArray(CompletableFuture[]::new));
    }

    @Override
    public String getName() {
        return "ItemOverrideProvider for " + dataOutput.getModId();
    }
}
