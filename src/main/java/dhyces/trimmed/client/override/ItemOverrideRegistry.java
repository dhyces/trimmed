package dhyces.trimmed.client.override;

import dhyces.trimmed.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.util.Util;
import net.minecraft.client.util.ModelIdentifier;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.LivingEntity;
import net.minecraft.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ItemOverrideRegistry {
    private static final Map<ModelIdentifier, Set<ItemOverrideProvider>> OVERRIDE_SET_MAP = new HashMap<>();

    public static Optional<ModelIdentifier> getOverrideModel(ItemStack itemStack, @Nullable ClientWorld world, @Nullable LivingEntity entity, int seed) {
        Set<ItemOverrideProvider> providers = OVERRIDE_SET_MAP.get(Util.getItemModelId(itemStack));
        for (ItemOverrideProvider provider : providers) {
            Optional<ModelIdentifier> identifier = provider.getModel(itemStack, world, entity, seed);
            if (identifier.isPresent()) {
                return identifier;
            }
        }
        return Optional.empty();
    }

    public static Optional<Set<ItemOverrideProvider>> getOverrides(ModelIdentifier modelId) {
        return Optional.ofNullable(OVERRIDE_SET_MAP.get(modelId));
    }

    static void addOverrideSet(ModelIdentifier identifier, Set<ItemOverrideProvider> set) {
        OVERRIDE_SET_MAP.put(identifier, set);
    }

    static void clearRegistry() {
        OVERRIDE_SET_MAP.clear();
    }
}
