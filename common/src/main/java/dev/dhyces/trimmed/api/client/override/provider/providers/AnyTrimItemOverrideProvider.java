package dev.dhyces.trimmed.api.client.override.provider.providers;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.client.override.provider.SimpleItemOverrideProvider;
import dev.dhyces.trimmed.impl.client.models.template.StringTemplate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponents;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class AnyTrimItemOverrideProvider extends SimpleItemOverrideProvider {
    public static final MapCodec<AnyTrimItemOverrideProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    StringTemplate.CODEC.fieldOf("model_id_template").forGetter(anyTrimItemOverrideProvider -> anyTrimItemOverrideProvider.modelIdTemplate)
            ).apply(instance, AnyTrimItemOverrideProvider::new)
    );

    private final StringTemplate modelIdTemplate;

    public AnyTrimItemOverrideProvider(StringTemplate modelIdTemplate) {
        this.modelIdTemplate = modelIdTemplate;
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
            return Optional.of(new ModelResourceLocation(new ResourceLocation(modelIdTemplate.process(s -> {
                if (s.equals("material_suffix")) {
                    return materialId.getPath();
                } else {
                    return null;
                }
            })), "inventory"));
        }
        return Optional.empty();
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.ANY_TRIM;
    }
}
