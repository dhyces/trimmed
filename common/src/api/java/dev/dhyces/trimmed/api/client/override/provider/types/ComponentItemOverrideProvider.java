package dev.dhyces.trimmed.api.client.override.provider.types;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.override.provider.SimpleItemOverrideProvider;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.component.DataComponentPatch;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class ComponentItemOverrideProvider extends SimpleItemOverrideProvider {
    public static final MapCodec<ComponentItemOverrideProvider> CODEC = RecordCodecBuilder.mapCodec(instance ->
            instance.group(
                    DataComponentPatch.CODEC.fieldOf("components").forGetter(componentItemOverrideProvider -> componentItemOverrideProvider.componentPatch),
                    CodecUtil.MODEL_IDENTIFIER_CODEC.fieldOf("model").forGetter(componentItemOverrideProvider -> componentItemOverrideProvider.model)
            ).apply(instance, ComponentItemOverrideProvider::new)
    );

    private final DataComponentPatch componentPatch;
    private final ModelResourceLocation model;

    public ComponentItemOverrideProvider(DataComponentPatch componentPatch, ModelResourceLocation modelId) {
        this.componentPatch = componentPatch;
        this.model = modelId;
    }

    @Override
    public ModelPair getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (!itemStack.getComponentsPatch().isEmpty()) {
            DataComponentPatch stackPatch = itemStack.getComponentsPatch();
            for (Map.Entry<DataComponentType<?>, Optional<?>> entry : componentPatch.entrySet()) {
                Optional<?> stackData = stackPatch.get(entry.getKey());
                Optional<?> testData = componentPatch.get(entry.getKey());
                if ((stackData == null) != (testData == null)) {
                    return ModelPair.EMPTY;
                }
                if (stackData.isEmpty() != testData.isEmpty()) {
                    return ModelPair.EMPTY;
                }
                if (stackData.isPresent() && testData.isPresent() && !stackData.get().equals(testData.get())) {
                    return ModelPair.EMPTY;
                }
            }
            return new ModelPair(null, model);
        }
        return ModelPair.EMPTY;
    }

    @Override
    public MapCodec<ComponentItemOverrideProvider> getCodec() {
        return CODEC;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ComponentItemOverrideProvider that = (ComponentItemOverrideProvider) o;
        return Objects.equals(componentPatch, that.componentPatch) && Objects.equals(model, that.model);
    }

    @Override
    public int hashCode() {
        return Objects.hash(componentPatch, model);
    }
}
