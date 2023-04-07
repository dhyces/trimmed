package dhyces.testmod.client.providers;

import com.google.common.base.Suppliers;
import com.mojang.serialization.Codec;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.block.BlockModelShaper;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.Property;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.function.Supplier;

public class BlockStateItemOverrideProvider implements ItemOverrideProvider {
    public static final Supplier<BlockStateItemOverrideProvider> LAZY_INSTANCE = Suppliers.memoize(BlockStateItemOverrideProvider::new);
    public static final Codec<BlockStateItemOverrideProvider> CODEC = Codec.unit(LAZY_INSTANCE);

    public BlockStateItemOverrideProvider() {}

    @Override
    public Optional<ModelResourceLocation> getModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (itemStack.hasTag() && itemStack.getTag().contains(BlockItem.BLOCK_STATE_TAG) && itemStack.getItem() instanceof BlockItem blockItem) {
            CompoundTag nbt = itemStack.getTag().getCompound(BlockItem.BLOCK_STATE_TAG);
            BlockState state = blockItem.getBlock().defaultBlockState();
            StateDefinition<Block, BlockState> stateManager = blockItem.getBlock().getStateDefinition();
            for (String key : nbt.getAllKeys()) {
                Property<?> property = stateManager.getProperty(key);
                if (property != null) {
                    state = with(state, property, nbt.get(key).getAsString());
                }
            }
            return Optional.of(BlockModelShaper.stateToModelLocation(state));
        }
        return Optional.empty();
    }

    private <T extends Comparable<T>> BlockState with(BlockState state, Property<T> property, String value) {
        return property.getValue(value).map(comparable -> state.setValue(property, comparable)).orElse(state);
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return MyProviderTypes.BLOCK_STATE;
    }
}
