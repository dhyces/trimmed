package dhyces.trimmed.api.client.override.provider.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public final class NbtItemOverrideProvider implements ItemOverrideProvider {
    public static final Codec<NbtItemOverrideProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NbtCompound.CODEC.fieldOf("nbt").forGetter(nbtItemOverrideProvider -> nbtItemOverrideProvider.nbt),
                    CodecUtil.MODEL_IDENTIFIER_CODEC.fieldOf("model").forGetter(nbtItemOverrideProvider -> nbtItemOverrideProvider.model)
            ).apply(instance, NbtItemOverrideProvider::new)
    );

    private final NbtCompound nbt;
    private final ModelIdentifier model;

    public NbtItemOverrideProvider(NbtCompound nbt, ModelIdentifier modelId) {
        this.nbt = nbt;
        this.model = modelId;
    }

    @Override
    public Optional<ModelIdentifier> getModel(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (itemStack.hasNbt()) {
            NbtCompound stackNbt = itemStack.getNbt();
            for (String key : nbt.getKeys()) {
                if (!stackNbt.contains(key) || !stackNbt.get(key).equals(nbt.get(key))) {
                    return Optional.empty();
                }
            }
            return Optional.of(model);
        }
        return Optional.empty();
    }

    @Override
    public Stream<ModelIdentifier> getModelsToBake() {
        return Stream.of(model);
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.NBT;
    }
}
