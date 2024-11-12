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
import net.minecraft.world.item.equipment.trim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;
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
    public ModelPair getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (!itemStack.has(DataComponents.EQUIPPABLE)) {
            return ModelPair.EMPTY;
        }

        Optional<ResourceLocation> materialIdOptional = Optional.ofNullable(itemStack.get(DataComponents.TRIM))
                .map(ArmorTrim::material)
                .flatMap(holder -> holder.unwrapKey().map(key -> key.location().withPath(itemStack.get(DataComponents.EQUIPPABLE).model().map(model -> holder.value().overrideArmorMaterials().get(model)).orElse(holder.value().assetName()))));
        if (materialIdOptional.isPresent()) {
            ResourceLocation id = ResourceLocation.parse(modelIdTemplate.process(s -> {
                if (s.equals("material_suffix")) {
                    return materialIdOptional.get().getPath();
                } else {
                    return null;
                }
            }));
            // TODO: Fabric is silly and forces custom models to be saved in top level as namespace:resource_id#fabric_resource
            return new ModelPair(id, new ModelResourceLocation(id.withPath(s -> s.substring(s.indexOf("/")+1)), "inventory"));
        }
        return ModelPair.EMPTY;
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.ANY_TRIM;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        AnyTrimItemOverrideProvider that = (AnyTrimItemOverrideProvider) o;
        return Objects.equals(modelIdTemplate, that.modelIdTemplate);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(modelIdTemplate);
    }
}
