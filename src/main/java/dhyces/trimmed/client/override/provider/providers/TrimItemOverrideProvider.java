package dhyces.trimmed.client.override.provider.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.util.CodecUtil;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.item.trim.ArmorTrim;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Identifier;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public class TrimItemOverrideProvider implements ItemOverrideProvider {
    public static final Codec<TrimItemOverrideProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    Identifier.CODEC.fieldOf("material").forGetter(provider -> provider.material),
                    CodecUtil.MODEL_IDENTIFIER_CODEC.fieldOf("model").forGetter(provider -> provider.model)
            ).apply(instance, TrimItemOverrideProvider::new)
    );

    private final Identifier material;
    private final ModelIdentifier model;

    public TrimItemOverrideProvider(Identifier material, ModelIdentifier model) {
        this.material = material;
        this.model = model;
    }

    @Override
    public Optional<ModelIdentifier> getModel(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        if (world == null) {
            return Optional.empty();
        }
        boolean isMaterial = ArmorTrim.getTrim(world.getRegistryManager(), itemStack)
                .map(ArmorTrim::getMaterial)
                .map(RegistryEntry::getKey)
                .orElse(Optional.empty())
                .map(RegistryKey::getValue)
                .map(material::equals)
                .orElse(false);
        return isMaterial ? Optional.of(model) : Optional.empty();
    }

    @Override
    public Stream<ModelIdentifier> getModelsToBake() {
        return Stream.of(model);
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.ARMOR_TRIM;
    }
}
