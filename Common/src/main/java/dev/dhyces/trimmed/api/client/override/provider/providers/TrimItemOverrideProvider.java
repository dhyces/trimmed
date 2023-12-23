package dev.dhyces.trimmed.api.client.override.provider.providers;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import dev.dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dev.dhyces.trimmed.api.client.override.provider.SimpleItemOverrideProvider;
import dev.dhyces.trimmed.api.util.CodecUtil;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import org.jetbrains.annotations.Nullable;

import java.util.Optional;
import java.util.stream.Stream;

public final class TrimItemOverrideProvider extends SimpleItemOverrideProvider {
    public static final Codec<TrimItemOverrideProvider> CODEC = RecordCodecBuilder.create(instance ->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("material").forGetter(provider -> provider.material),
                    CodecUtil.MODEL_IDENTIFIER_CODEC.fieldOf("model").forGetter(provider -> provider.model)
            ).apply(instance, TrimItemOverrideProvider::new)
    );

    private final ResourceLocation material;
    private final ModelResourceLocation model;

    public TrimItemOverrideProvider(ResourceLocation material, ModelResourceLocation model) {
        this.material = material;
        this.model = model;
    }

    @Override
    public Optional<ModelResourceLocation> getModelLocation(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        if (world == null) {
            return Optional.empty();
        }
        boolean isMaterial = ArmorTrim.getTrim(world.registryAccess(), itemStack, true)
                .map(ArmorTrim::material)
                .flatMap(Holder::unwrapKey)
                .map(ResourceKey::location)
                .map(material::equals)
                .orElse(false);
        return isMaterial ? Optional.of(model) : Optional.empty();
    }

    @Override
    public Stream<ModelResourceLocation> getModelsToBake() {
        return Stream.of(model);
    }

    @Override
    public ItemOverrideProviderType<?> getType() {
        return ItemOverrideProviderType.ARMOR_TRIM;
    }
}
