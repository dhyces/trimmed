package dhyces.testmod;

import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModTrimMaterials {

    public static final RegistryKey<ArmorTrimMaterial> ECHO = registryKey("echo");
    public static final RegistryKey<ArmorTrimMaterial> BLAZE = registryKey("blaze");
    public static final RegistryKey<ArmorTrimMaterial> SHELL = registryKey("shell");
    public static final RegistryKey<ArmorTrimMaterial> PRISMARINE = registryKey("prismarine");
    public static final RegistryKey<ArmorTrimMaterial> GLOW = registryKey("glow");

    public static void bootstrap(Registerable<ArmorTrimMaterial> context) {
//        context.register(ECHO, ArmorTrimMaterial.of("echo", Items.ECHO_SHARD, 0.91f, Text.translatable("trimmed.trim_material.echo").fillStyle(Style.EMPTY.withColor(0x0A4F5F))));
//        context.register(BLAZE, ArmorTrimMaterial.of("blaze", Items.BLAZE_POWDER, 0.92f, Text.translatable("trimmed.trim_material.blaze").fillStyle(Style.EMPTY.withColor(0xFCA100))));
//        context.register(SHELL, ArmorTrimMaterial.of("shell", Items.NAUTILUS_SHELL, 0.93f, Text.translatable("trimmed.trim_material.shell").fillStyle(Style.EMPTY.withColor(0xD17E7E))));
//        context.register(PRISMARINE, ArmorTrimMaterial.of("prismarine", Items.PRISMARINE_CRYSTALS, 0.94f, Text.translatable("trimmed.trim_material.prismarine").fillStyle(Style.EMPTY.withColor(0xB2D5C8))));
//        context.register(GLOW, ArmorTrimMaterial.of("glow", Items.GLOW_INK_SAC, 0.95f, Text.translatable("trimmed.trim_material.glow").fillStyle(Style.EMPTY.withColor(0x7EFCBE))));
    }

    private static RegistryKey<ArmorTrimMaterial> registryKey(String id) {
        return RegistryKey.of(RegistryKeys.TRIM_MATERIAL, new Identifier(TrimmedTest.MODID, id));
    }
}
