package dev.dhyces.trimmed.api.client.override.provider;

import dev.dhyces.trimmed.api.services.ApiServices;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public abstract class SimpleItemOverrideProvider implements ItemOverrideProvider {
    @Override
    public Optional<BakedModel> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        ModelPair pair = getModelLocation(itemStack, world, entity, seed);
        return pair.getTopLevelModelId().map(Minecraft.getInstance().getModelManager()::getModel)
                .filter(model -> model != Minecraft.getInstance().getModelManager().getMissingModel())
                .or(() -> pair.getResourceId().map(ApiServices.MODEL_HELPER::getModel));
    }

    public abstract ModelPair getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed);

    public record ModelPair(@Nullable ResourceLocation resourceId, @Nullable ModelResourceLocation topLevelModelId) {
        public static final ModelPair EMPTY = new ModelPair(null, null);

        public Optional<ResourceLocation> getResourceId() {
            return Optional.ofNullable(resourceId);
        }
        public Optional<ModelResourceLocation> getTopLevelModelId() {
            return Optional.ofNullable(topLevelModelId);
        }
    }
}
