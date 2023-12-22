package dhyces.trimmed.impl;

import dhyces.trimmed.api.TrimmedClientApi;
import dhyces.trimmed.api.TrimmedClientMapApi;
import dhyces.trimmed.api.client.UncheckedClientMaps;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProvider;
import dhyces.trimmed.api.client.override.provider.ItemOverrideProviderType;
import dhyces.trimmed.impl.client.maps.ClientMapKey;
import dhyces.trimmed.impl.client.models.override.provider.ItemOverrideProviderRegistry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.armortrim.ArmorTrim;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Optional;

public final class TrimmedClientApiImpl implements TrimmedClientApi {
    @Override
    public <T extends ItemOverrideProvider> ItemOverrideProviderType<T> registerItemOverrideType(ResourceLocation id, ItemOverrideProviderType<T> providerType) {
        ItemOverrideProviderRegistry.register(id, providerType);
        return providerType;
    }

    @Override
    public Optional<String> getArmorTrimSuffix(RegistryAccess registryAccess, ItemStack stack) {
        return ArmorTrim.getTrim(registryAccess, stack, true)
                .map(ArmorTrim::material)
                .map(holder -> {
                    if (stack.getItem() instanceof ArmorItem armorItem) {
                        TrimMaterial material = holder.value();
                        ArmorMaterial armorMaterial = armorItem.getMaterial();

                        // Support overriding vanilla armor material overrides
                        ClientMapKey mapKey = UncheckedClientMaps.armorMaterialOverride(holder.unwrapKey().get());
                        String value = TrimmedClientMapApi.INSTANCE.getUncheckedClientValue(mapKey, new ResourceLocation(armorMaterial.getName()));

                        if (value != null) {
                            return value;
                        } else if (armorMaterial instanceof ArmorMaterials enumMaterial) {
                            return material.overrideArmorMaterials().get(enumMaterial);
                        }
                    }
                    return null;
                });
    }
}