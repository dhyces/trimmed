package dhyces.trimmed.api.client.override.provider;

import com.mojang.serialization.Codec;
import dhyces.trimmed.impl.client.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public interface ItemOverrideProvider {
    Codec<ItemOverrideProvider> CODEC = ItemOverrideProviderRegistry.CODEC.dispatch("type", ItemOverrideProvider::getType, ItemOverrideProviderType::getCodec);

    Optional<BakedModel> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed);

    /**
     *
     * @return Any models that need to be baked
     */
    default Stream<ModelResourceLocation> getModelsToBake() {
        return Stream.of();
    }

    ItemOverrideProviderType<?> getType();
}
