package dhyces.trimmed.api.client.override.provider.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.api.client.override.provider.SimpleItemOverrideProvider;
import dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public final class NbtItemOverrideProvider extends SimpleItemOverrideProvider {
    public static final Codec<NbtItemOverrideProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    CompoundTag.CODEC.fieldOf("nbt").forGetter(nbtItemOverrideProvider -> nbtItemOverrideProvider.nbt),
                    CodecUtil.MODEL_IDENTIFIER_CODEC.fieldOf("model").forGetter(nbtItemOverrideProvider -> nbtItemOverrideProvider.model)
            ).apply(instance, NbtItemOverrideProvider::new)
    );

    private final CompoundTag nbt;
    private final ModelResourceLocation model;

    public NbtItemOverrideProvider(CompoundTag nbt, ModelResourceLocation modelId) {
        this.nbt = nbt;
        this.model = modelId;
    }

    @Override
    public Optional<ModelResourceLocation> getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (itemStack.hasTag()) {
            CompoundTag stackNbt = itemStack.getTag();
            for (String key : nbt.getAllKeys()) {
                if (!stackNbt.contains(key) || !stackNbt.get(key).equals(nbt.get(key))) {
                    return Optional.empty();
                }
            }
            return Optional.of(model);
        }
        return Optional.empty();
    }

    @Override
    public Stream<ModelResourceLocation> getModelsToBake() {
        return Stream.of(model);
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.NBT;
    }
}
