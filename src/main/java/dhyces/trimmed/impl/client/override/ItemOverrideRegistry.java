package dhyces.trimmed.impl.client.override;

import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.util.Util;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ItemOverrideRegistry {
    private static final Map<ModelResourceLocation, Set<ItemOverrideProvider>> OVERRIDE_SET_MAP = new HashMap<>();

    public static Optional<ModelResourceLocation> getOverrideModel(ItemStack itemStack, @Nullable ClientLevel world, @Nullable LivingEntity entity, int seed) {
        Set<ItemOverrideProvider> providers = OVERRIDE_SET_MAP.get(Util.getItemModelId(itemStack));
        if (providers == null) {
            return Optional.empty();
        }

        for (ItemOverrideProvider provider : providers) {
            Optional<ModelResourceLocation> identifier = provider.getModel(itemStack, world, entity, seed);
            if (identifier.isPresent()) {
                return identifier;
            }
        }
        return Optional.empty();
    }

    public static Optional<Set<ItemOverrideProvider>> getOverrides(ModelResourceLocation modelId) {
        return Optional.ofNullable(OVERRIDE_SET_MAP.get(modelId));
    }

    static void addOverrideSet(ModelResourceLocation identifier, Set<ItemOverrideProvider> set) {
        OVERRIDE_SET_MAP.put(identifier, set);
    }

    static void clearRegistry() {
        OVERRIDE_SET_MAP.clear();
    }
}
