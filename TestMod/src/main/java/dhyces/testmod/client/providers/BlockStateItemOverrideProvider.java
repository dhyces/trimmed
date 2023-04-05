package dhyces.testmod.client.providers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.client.override.provider.ItemOverrideProviderType;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.render.block.BlockModels;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockStateItemOverrideProvider implements ItemOverrideProvider {
    public static final Supplier<BlockStateItemOverrideProvider> LAZY_INSTANCE = Suppliers.memoize(BlockStateItemOverrideProvider::new);
    public static final Codec<BlockStateItemOverrideProvider> CODEC = Codec.unit(LAZY_INSTANCE);

    public BlockStateItemOverrideProvider() {}

    @Override
    public Optional<ModelIdentifier> getModel(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (itemStack.hasNbt() && itemStack.getNbt().contains(BlockItem.BLOCK_STATE_TAG_KEY) && itemStack.getItem() instanceof BlockItem blockItem) {
            NbtCompound nbt = itemStack.getNbt().getCompound(BlockItem.BLOCK_STATE_TAG_KEY);
            BlockState state = blockItem.getBlock().getDefaultState();
            StateManager<Block, BlockState> stateManager = blockItem.getBlock().getStateManager();
            for (String key : nbt.getKeys()) {
                Property<?> property = stateManager.getProperty(key);
                if (property != null) {
                    state = with(state, property, nbt.get(key).asString());
                }
            }
            return Optional.of(BlockModels.getModelId(state));
        }
        return Optional.empty();
    }

    private <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String value) {
        return property.parse(value).map(comparable -> state.with(property, comparable)).orElse(state);
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return MyProviderTypes.BLOCK_STATE;
    }
}
