package dev.dhyces.testmod.client.providers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockStateItemOverrideProvider implements ItemOverrideProvider {
    public static final Supplier<BlockStateItemOverrideProvider> LAZY_INSTANCE = Suppliers.memoize(BlockStateItemOverrideProvider::new);
    public static final MapCodec<BlockStateItemOverrideProvider> CODEC = MapCodec.unit(LAZY_INSTANCE);

    public BlockStateItemOverrideProvider() {}

    @Override
    public Optional<BakedModel> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (itemStack.has(DataComponents.BLOCK_STATE) && itemStack.getItem() instanceof BlockItem blockItem) {
            BlockState state = itemStack.get(DataComponents.BLOCK_STATE).apply(blockItem.getBlock().defaultBlockState());
            return Optional.of(Minecraft.getInstance().getModelManager().getBlockModelShaper().getBlockModel(state));
        }
        return Optional.empty();
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return MyProviderTypes.BLOCK_STATE;
    }
}
