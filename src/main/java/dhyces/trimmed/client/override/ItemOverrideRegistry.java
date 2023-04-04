package dhyces.trimmed.client.override;

import net.minecraft.client.util.ModelIdentifier;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class ItemOverrideRegistry {
    private static final Map<ModelIdentifier, OverrideSet> OVERRIDE_SET_MAP = new HashMap<>();

    public static void addOverrideSet(ModelIdentifier identifier, OverrideSet set) {
        OVERRIDE_SET_MAP.put(identifier, set);
    }

    public static Optional<OverrideSet> getOverrideSet(ModelIdentifier identifier) {
        return Optional.ofNullable(OVERRIDE_SET_MAP.get(identifier));
    }

    static void clearRegistry() {
        OVERRIDE_SET_MAP.clear();
    }
}
