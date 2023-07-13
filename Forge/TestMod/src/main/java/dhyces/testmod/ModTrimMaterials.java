package dhyces.testmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.HashMap;

public class ModTrimMaterials {

    public static final ResourceKey<TrimMaterial> ECHO = registryKey("echo");
    public static final ResourceKey<TrimMaterial> BLAZE = registryKey("blaze");
    public static final ResourceKey<TrimMaterial> SHELL = registryKey("shell");
    public static final ResourceKey<TrimMaterial> PRISMARINE = registryKey("prismarine");
    public static final ResourceKey<TrimMaterial> GLOW = registryKey("glow");
    public static final ResourceKey<TrimMaterial> ADAMANTIUM = registryKey("adamantium");

    private static ResourceKey<TrimMaterial> registryKey(String id) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(TrimmedTest.MODID, id));
    }
}
