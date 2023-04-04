package dhyces.trimmed.client.override;

import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class OverrideSet {
    private ModelIdentifier modelId;
    private Set<ItemOverrideProvider> providers;

    public OverrideSet(ModelIdentifier modelId) {
        this.modelId = modelId;
        this.providers = new LinkedHashSet<>();
    }

    void addProviders(List<ItemOverrideProvider> providers) {
        this.providers.addAll(providers);
    }

    public Optional<ModelIdentifier> testProviders(ItemStack stack, @Nullable ClientWorld world, @Nullable LivingEntity livingEntity, int seed) {
        for (ItemOverrideProvider provider : providers) {
            Optional<ModelIdentifier> optionalModel = provider.getModel(stack, world, livingEntity, seed);
            if (optionalModel.isPresent()) {
                return optionalModel;
            }
        }
        return Optional.empty();
    }

    public Stream<ModelIdentifier> getModelsToBake() {
        return providers.stream().flatMap(ItemOverrideProvider::getModelsToBake);
    }
}
