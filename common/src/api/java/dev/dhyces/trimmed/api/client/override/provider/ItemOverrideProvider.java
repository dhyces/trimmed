package dev.dhyces.trimmed.api.client.override.provider;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.Set;
import java.util.function.Function;

public interface ItemOverrideProvider {
    Codec<ItemOverrideProvider> CODEC = CodecUtil.ITEM_OVERRIDE_PROVIDER_REGISTRY.dispatch(ItemOverrideProvider::getCodec, Function.identity());
    MapCodec<Set<ItemOverrideProvider>> SET_MAP_CODEC = CodecUtil.setOf(CODEC).fieldOf("values");
    Codec<Set<ItemOverrideProvider>> SET_MAP_CODEC_CODEC = SET_MAP_CODEC.codec();

    Optional<BakedModel> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed);

    MapCodec<? extends ItemOverrideProvider> getCodec();
}
