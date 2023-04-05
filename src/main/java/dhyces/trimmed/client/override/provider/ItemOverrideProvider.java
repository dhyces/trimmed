package dhyces.trimmed.client.override.provider;

import com.mojang.serialization.Codec;
import net.minecraft.client.render.model.ModelLoader;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public interface ItemOverrideProvider {
    Codec<ItemOverrideProvider> CODEC = ItemOverrideProviderRegistry.CODEC.dispatch("type", ItemOverrideProvider::getType, ItemOverrideProviderType::getCodec);

    Optional<ModelIdentifier> getModel(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed);

    /**
     *
     * @return Any models that need to be baked
     */
    default Stream<ModelIdentifier> getModelsToBake() {
        return Stream.of();
    }

    ItemOverrideProviderType<?> getType();
}
