package dhyces.trimmed;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.armortrim.TrimMaterial;

import java.util.Optional;

public class ModTrimMaterials {

    public static final ResourceKey<TrimMaterial> ECHO = registryKey("echo");
    public static final ResourceKey<TrimMaterial> BLAZE = registryKey("blaze");
    public static final ResourceKey<TrimMaterial> SHELL = registryKey("shell");
    public static final ResourceKey<TrimMaterial> PRISMARINE = registryKey("prismarine");
    public static final ResourceKey<TrimMaterial> GLOW = registryKey("glow");

    public static void bootstrap(BootstapContext<TrimMaterial> context) {
        context.register(ECHO, TrimMaterial.create("echo", Items.ECHO_SHARD, 0.91f, Optional.empty(), Component.translatable("trimmed.trim_material.echo").withStyle(Style.EMPTY.withColor(0x0A4F5F))));
        context.register(BLAZE, TrimMaterial.create("blaze", Items.BLAZE_POWDER, 0.92f, Optional.empty(), Component.translatable("trimmed.trim_material.blaze").withStyle(Style.EMPTY.withColor(0xFCA100))));
        context.register(SHELL, TrimMaterial.create("shell", Items.NAUTILUS_SHELL, 0.93f, Optional.empty(), Component.translatable("trimmed.trim_material.shell").withStyle(Style.EMPTY.withColor(0xD17E7E))));
        context.register(PRISMARINE, TrimMaterial.create("prismarine", Items.PRISMARINE_CRYSTALS, 0.94f, Optional.empty(), Component.translatable("trimmed.trim_material.prismarine").withStyle(Style.EMPTY.withColor(0xB2D5C8))));
        context.register(GLOW, TrimMaterial.create("glow", Items.GLOW_INK_SAC, 0.95f, Optional.empty(), Component.translatable("trimmed.trim_material.glow").withStyle(Style.EMPTY.withColor(0x7EFCBE))));

    }

    private static ResourceKey<TrimMaterial> registryKey(String id) {
        return ResourceKey.create(Registries.TRIM_MATERIAL, new ResourceLocation(Trimmed.MODID, id));
    }
}
