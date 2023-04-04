package dhyces.trimmed.client.override.provider.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.client.override.provider.ItemOverrideProviderType;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;

public class NbtItemOverrideProvider implements ItemOverrideProvider {
    public static final Codec<NbtItemOverrideProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    NbtCompound.CODEC.fieldOf("nbt").forGetter(),
                    Identifier.CODEC.
            )
    );

    @Override
    public Optional<ModelIdentifier> getModel(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        return Optional.empty();
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return null;
    }
}
