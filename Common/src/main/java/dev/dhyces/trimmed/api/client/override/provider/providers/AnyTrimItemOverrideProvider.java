package dev.dhyces.trimmed.api.client.override.provider.providers;

import com.mojang.datafixers.util.Pair;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.client.override.provider.SimpleItemOverrideProvider;
import dev.dhyces.trimmed.api.maps.ImmutableEntry;
import dev.dhyces.trimmed.api.util.CodecUtil;
import dev.dhyces.trimmed.impl.client.models.template.ModelTemplateManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.model.BlockModel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

public class AnyTrimItemOverrideProvider extends SimpleItemOverrideProvider {
    public static final MapCodec<AnyTrimItemOverrideProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("template").forGetter(anyTrimItemOverrideProvider -> anyTrimItemOverrideProvider.templateId),
                    ResourceLocation.CODEC.fieldOf("trim_texture").forGetter(anyTrimItemOverrideProvider -> anyTrimItemOverrideProvider.trimTexture),
                    CodecUtil.setOf(ResourceLocation.CODEC).optionalFieldOf("exclude_palettes", Set.of()).forGetter(anyTrimItemOverrideProvider -> anyTrimItemOverrideProvider.excludedTextures)
            ).apply(instance, AnyTrimItemOverrideProvider::new)
    );

    private final ResourceLocation templateId;
    private final ResourceLocation trimTexture;
    private final Set<ResourceLocation> excludedTextures;
    private ResourceLocation id;

    public AnyTrimItemOverrideProvider(ResourceLocation templateId, ResourceLocation trimTexture, Set<ResourceLocation> excludedTextures) {
        this.templateId = templateId;
        this.trimTexture = trimTexture;
        this.excludedTextures = excludedTextures;
    }

    @Override
    public Optional<BakedModel> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        return super.getModel(itemStack, world, entity, seed)
                .filter(model -> model != Minecraft.getInstance().getModelManager().getMissingModel());
    }

    @Override
    public Optional<ModelResourceLocation> getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (!(itemStack.getItem() instanceof ArmorItem armorItem)) {
            return Optional.empty();
        }

        Optional<ResourceLocation> materialIdOptional = Optional.ofNullable(itemStack.get(DataComponents.TRIM))
                .map(ArmorTrim::material)
                .flatMap(holder -> holder.unwrapKey().map(key -> key.location().withPath(holder.value().overrideArmorMaterials().getOrDefault(armorItem.getMaterial(), holder.value().assetName()))));
        if (materialIdOptional.isPresent()) {
            ResourceLocation materialId = materialIdOptional.get();
            return Optional.of(new ModelResourceLocation(id.withSuffix("_" + materialId.getPath() + "_trim"), "inventory"));
        }
        return Optional.empty();
    }

    @Override
    public void finish(ResourceLocation id) {
        this.id = id;
//        ModelTemplateManager.addTemplate(templateId, (reader, modelConsumer) -> {
//            String rawData = reader.lines().collect(Collectors.joining());
//            Pair<String, String> item = Pair.of("item_texture", id.withPrefix("item/").toString());
//
//            for (ImmutableEntry<ResourceLocation, String> material : PERMUTATIONS) {
//                if (excludedTextures.contains(material.getKey())) {
//                    continue;
//                }
//
//                List<Pair<String, String>> replacers = new ArrayList<>();
//                replacers.add(item);
//                replacers.add(Pair.of("material", material.getValue()));
//                replacers.add(Pair.of("trim_texture", trimTexture.toString()));
//
//                modelConsumer.accept(id.withSuffix("_" + material.getValue() + "_trim"), () -> BlockModel.fromString(GroovyReplacer.replace(rawData, replacers)));
//            }
//        });
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.ANY_TRIM;
    }
}
