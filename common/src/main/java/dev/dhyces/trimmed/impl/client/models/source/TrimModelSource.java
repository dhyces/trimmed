package dev.dhyces.trimmed.impl.client.models.source;

import com.google.common.collect.ImmutableList;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.TrimmedClientMapApi;
import dev.dhyces.trimmed.api.client.ClientMapKeys;
import dev.dhyces.trimmed.api.client.ClientMapTypes;
import dev.dhyces.trimmed.api.maps.MapHolder;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import it.unimi.dsi.fastutil.objects.Object2ObjectArrayMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectFunction;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;

import java.util.Collection;
import java.util.Map;

public record TrimModelSource(ResourceLocation template, MapHolder<ResourceLocation, ResourceLocation> textures, MapHolder<ResourceLocation, ResourceLocation> overrides) implements ModelSource {
    public static final MapCodec<TrimModelSource> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("template").forGetter(TrimModelSource::template),
                    TrimmedClientMapApi.getInstance().simpleCodec(ClientMapTypes.TEXTURE_MAPPING).fieldOf("textures").forGetter(TrimModelSource::textures),
                    TrimmedClientMapApi.getInstance().simpleCodec(ClientMapTypes.TEXTURE_MAPPING).fieldOf("override_textures").forGetter(TrimModelSource::overrides)
            ).apply(instance, TrimModelSource::new)
    );

    private static final MapHolder<ResourceLocation, String> MATERIALS = TrimmedClientMapApi.getInstance().getSimpleMap(ClientMapKeys.MATERIAL_SUFFIXES);
    private static final MapHolder<ResourceLocation, String> DARKER_MATERIALS = TrimmedClientMapApi.getInstance().getSimpleMap(ClientMapKeys.DARKER_MATERIAL_SUFFIXES);
    public static final String ITEM_TEXTURE = "item_texture";
    public static final String TRIM_TEXTURE = "trim_overlay_texture";
    public static final String MATERIAL_SUFFIX = "material_suffix";

    @Override
    public Collection<NamedModel> generate(ResourceManager resourceManager, ModelTemplateManager templateManager) {
        ImmutableList.Builder<NamedModel> modelBuilder = ImmutableList.builder();
        // This should be safe, it'll just replace the values of these keys each time
        Object2ObjectFunction<String, String> replacer = new Object2ObjectArrayMap<>();
        for (Map.Entry<ResourceLocation, ResourceLocation> textureEntry : textures.getMap().entrySet()) {
            replacer.put(ITEM_TEXTURE, textureEntry.getKey().toString());
            replacer.put(TRIM_TEXTURE, textureEntry.getValue().toString());
            for (Map.Entry<ResourceLocation, String> materialEntry : MATERIALS.getMap().entrySet()) {
                String suffix = materialEntry.getValue();
                if (DARKER_MATERIALS.getMap().containsKey(materialEntry.getKey())) {
                    if (overrides.isBound() && overrides.getMap().containsKey(materialEntry.getKey())) {
                        suffix = MATERIALS.getMap().get(overrides.getMap().get(materialEntry.getKey()));
                    } else {
                        continue;
                    }
                }
                replacer.put(MATERIAL_SUFFIX, suffix);
                String processed = templateManager.process(template, replacer);
                modelBuilder.add(
                        NamedModel.of(
                                textureEntry.getKey().withSuffix("_" + suffix + "_trim"),
                                BlockModel.fromString(processed)
                        )
                );
            }
        }
        return modelBuilder.build();
    }

    @Override
    public MapCodec<? extends ModelSource> codec() {
        return CODEC;
    }
}
