package dev.dhyces.trimmed.api.data.model.source;

import com.google.common.base.Preconditions;
import com.google.gson.JsonElement;
import com.mojang.serialization.JsonOps;
import dev.dhyces.trimmed.api.TrimmedReference;
import dev.dhyces.trimmed.api.client.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.client.map.ClientMapKeys;
import dev.dhyces.trimmed.api.client.map.ClientMapTypes;
import dev.dhyces.trimmed.api.client.models.source.ModelSource;
import dev.dhyces.trimmed.api.client.models.source.types.TrimModelSource;
import dev.dhyces.trimmed.api.maps.MapKey;
import dev.dhyces.trimmed.api.services.ApiServices;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.nio.file.Path;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class BaseModelSourceDataProvider implements DataProvider {
    private static final ResourceLocation TWO_LAYER_TEMPLATE = TrimmedReference.id("item/two_layer_trim");
    private static final ResourceLocation THREE_LAYER_TEMPLATE = TrimmedReference.id("item/three_layer_trim");
    protected final PackOutput packOutput;
    protected final PackOutput.PathProvider pathProvider;
    protected final String modid;
    protected final Map<ResourceLocation, ModelSource> modelSources;

    public BaseModelSourceDataProvider(PackOutput packOutput, String modid) {
        this.packOutput = packOutput;
        this.pathProvider = packOutput.createPathProvider(PackOutput.Target.RESOURCE_PACK, TrimmedReference.MODEL_GENERATORS_DIRECTORY);
        this.modid = modid;
        this.modelSources = new Object2ObjectLinkedOpenHashMap<>();
    }

    protected abstract void addModelSources();

    public void addTwoLayerTrimsSource(ResourceLocation armorMaterial) {
        ResourceLocation id = armorMaterial.withSuffix("_armor");
        addTwoLayerTrimsSource(id, id, armorMaterial);
    }

    public void addThreeLayerTrimsSource(ResourceLocation armorMaterial) {
        ResourceLocation id = armorMaterial.withSuffix("_armor");
        addThreeLayerTrimsSource(id, id, armorMaterial);
    }

    public void addTwoLayerTrimsSource(ResourceLocation id, ResourceLocation overlayTextures, ResourceLocation overrideTextures) {
        addTrimsSource(id, TWO_LAYER_TEMPLATE, ClientMapKeys.TRIM_OVERLAYS.makeSubKey(overlayTextures), ClientMapKeys.TRIM_MATERIAL_OVERRIDES.makeSubKey(overrideTextures));
    }

    public void addThreeLayerTrimsSource(ResourceLocation id, ResourceLocation overlayTextures, ResourceLocation overrideTextures) {
        addTrimsSource(id, THREE_LAYER_TEMPLATE, ClientMapKeys.TRIM_OVERLAYS.makeSubKey(overlayTextures), ClientMapKeys.TRIM_MATERIAL_OVERRIDES.makeSubKey(overrideTextures));
    }

    public void addTrimsSource(ResourceLocation id, ResourceLocation template, ResourceLocation overlayTextures, ResourceLocation overrideTextures) {
        addTrimsSource(id, template, ClientMapKeys.TRIM_OVERLAYS.makeSubKey(overlayTextures), ClientMapKeys.TRIM_MATERIAL_OVERRIDES.makeSubKey(overrideTextures));
    }

    public void addTrimsSource(ResourceLocation id, ResourceLocation template, MapKey<ResourceLocation, ResourceLocation> overlayTextures, MapKey<ResourceLocation, ResourceLocation> overrideTextures) {
        Preconditions.checkArgument(overlayTextures.getType() == ClientMapTypes.TEXTURE_MAPPING && overrideTextures.getType() == ClientMapTypes.TEXTURE_MAPPING, "Textures must use the \"texture_mapping\" type");
        add(id, new TrimModelSource(template, TrimmedClientMapApi.getInstance().getSimpleMap(overlayTextures), TrimmedClientMapApi.getInstance().getSimpleMap(overrideTextures)));
    }

    public void add(ResourceLocation id, ModelSource modelSource) {
        if (modelSources.containsKey(id)) {
            throw new IllegalArgumentException("ModelSource already registered with id \"" + id + "\"");
        }

        modelSources.put(id, modelSource);
    }

    /**
     * Used in the Neo specific provider for adding to existing files
     * @param id
     */
    protected void onAdd(ResourceLocation id) {}

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        addModelSources();
        return CompletableFuture.allOf(
                modelSources.entrySet().stream().map((entry) -> {
                    JsonElement jsonElement = ApiServices.INTERNAL_CODECS.getModelSourceRegistryCodec().encodeStart(JsonOps.INSTANCE, entry.getValue()).getOrThrow();
                    Path path = pathProvider.json(entry.getKey());
                    return DataProvider.saveStable(output, jsonElement, path);
                }).toArray(CompletableFuture[]::new)
        );
    }

    @Override
    public String getName() {
        return "ModelSourceDataProvider for " + modid;
    }
}
