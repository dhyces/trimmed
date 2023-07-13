package dhyces.testmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.armortrim.TrimPattern;

public class ModTrimPatterns {

    public static final ResourceKey<TrimPattern> SPIRAL = registryKey("spiral");

    private static ResourceKey<TrimPattern> registryKey(String id) {
        return ResourceKey.create(Registries.TRIM_PATTERN, new ResourceLocation(TrimmedTest.MODID, id));
    }
}
