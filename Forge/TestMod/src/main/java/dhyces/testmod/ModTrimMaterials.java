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

    public static void bootstrap(BootstapContext<TrimMaterial> context) {
        context.register(ECHO, new TrimMaterial("trimmed_testmod-echo", Items.ECHO_SHARD.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.echo").setStyle(Style.EMPTY.withColor(0x0A4F5F))));
        context.register(BLAZE, new TrimMaterial("trimmed_testmod-blaze", Items.BLAZE_POWDER.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.blaze").setStyle(Style.EMPTY.withColor(0xFCA100))));
        context.register(SHELL, new TrimMaterial("trimmed_testmod-shell", Items.NAUTILUS_SHELL.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.shell").setStyle(Style.EMPTY.withColor(0xD17E7E))));
        context.register(PRISMARINE, new TrimMaterial("trimmed_testmod-prismarine", Items.PRISMARINE_CRYSTALS.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.prismarine").setStyle(Style.EMPTY.withColor(0xB2D5C8))));
        context.register(GLOW, new TrimMaterial("trimmed_testmod-glow", Items.GLOW_INK_SAC.builtInRegistryHolder(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.glow").setStyle(Style.EMPTY.withColor(0x7EFCBE))));
        context.register(ADAMANTIUM, new TrimMaterial("trimmed_testmod-adamantium", ModItems.ADAMANTIUM.getHolder().get(), -1, new HashMap<>(), Component.translatable("trimmed.trim_material.adamantium").setStyle(Style.EMPTY.withColor(0x9d2638))));
    }

    private static ResourceKey<TrimMaterial> registryKey(String id) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(TrimmedTest.MODID, id));
    }
}
