package dhyces.trimmed.api.client;

import dhyces.trimmed.Trimmed;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;

public class UncheckedClientMaps {
    public static final ClientMapKey CUSTOM_TRIM_PERMUTATIONS = ClientMapKey.of(Trimmed.id("custom_trim_material_permutations"));
    public static final ClientMapKey VANILLA_TRIM_PERMUTATIONS = ClientMapKey.of(Trimmed.id("vanilla_trim_material_permutations"));

    public static final ClientMapKey armorMaterialOverride(ResourceKey<TrimMaterial> trimMaterialKey) {
        return ClientMapKey.of(trimMaterialKey.location().withPrefix("armor_material_overrides/"));
    }
}
