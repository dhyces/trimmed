package dev.dhyces.testmod.data;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.registry.ModItems;
import dev.dhyces.trimmed.api.data.TrimDatagenSuite;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class TestTrimDatagenSuite extends TrimDatagenSuite {
    public TestTrimDatagenSuite(GatherDataEvent event, String modid, @Nullable BiConsumer<String, String> translationConsumer) {
        super(event, modid, translationConsumer);
    }

    @Override
    public void generate() {
        makePattern(ResourceKey.create(Registries.TRIM_PATTERN, TrimmedTest.id("testertester")), ModItems.SCANNER.get(), false, patternConfig -> patternConfig.createCopyRecipe(Items.IRON_BOOTS));
    }
}
