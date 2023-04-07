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

    public static void bootstrap(BootstapContext<TrimMaterial> context) {
        context.register(ECHO, new TrimMaterial("echo", Items.ECHO_SHARD.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.echo").setStyle(Style.EMPTY.withColor(0x0A4F5F))));
        context.register(BLAZE, new TrimMaterial("blaze", Items.BLAZE_POWDER.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.blaze").setStyle(Style.EMPTY.withColor(0xFCA100))));
        context.register(SHELL, new TrimMaterial("shell", Items.NAUTILUS_SHELL.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.shell").setStyle(Style.EMPTY.withColor(0xD17E7E))));
        context.register(PRISMARINE, new TrimMaterial("prismarine", Items.PRISMARINE_CRYSTALS.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.prismarine").setStyle(Style.EMPTY.withColor(0xB2D5C8))));
        context.register(GLOW, new TrimMaterial("glow", Items.GLOW_INK_SAC.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.glow").setStyle(Style.EMPTY.withColor(0x7EFCBE))));
    }

    private static ResourceKey<TrimMaterial> registryKey(String id) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(TrimmedTest.MODID, id));
    }
}
