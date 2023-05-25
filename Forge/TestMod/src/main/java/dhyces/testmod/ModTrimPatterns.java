package dhyces.testmod;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.armortrim.TrimPattern;

public class ModTrimPatterns {

    public static final ResourceKey<TrimPattern> SPIRAL = ResourceKey.create(Registries.TRIM_PATTERN, TrimmedTest.id("spiral"));

    public static void bootstrap(BootstapContext<TrimPattern> context) {
        context.register(SPIRAL, new TrimPattern(TrimmedTest.id("spiral"), ModItems.SPIRAL_PATTERN.get().builtInRegistryHolder(), Component.translatable("trimmed.trim_pattern.spiral")));
    }
}
