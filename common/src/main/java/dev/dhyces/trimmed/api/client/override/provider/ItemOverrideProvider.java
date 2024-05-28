package dev.dhyces.trimmed.api.client.override.provider;

import com.mojang.serialization.Codec;
import dev.dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public interface ItemOverrideProvider {
    Codec<ItemOverrideProvider> CODEC = ItemOverrideProviderRegistry.CODEC.dispatch("type", ItemOverrideProvider::getType, ItemOverrideProviderType::getCodec);
    Codec<List<ItemOverrideProvider>> LIST_CODEC = CODEC.listOf().fieldOf("values").codec();

    Optional<BakedModel> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed);

    ItemOverrideProviderType<?> getType();
}
