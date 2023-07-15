package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import dhyces.testmod.TrimmedTest;
import dhyces.trimmed.api.data.TrimDatagenSuite;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Items;
import net.minecraftforge.data.event.GatherDataEvent;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

public class TestTrimDatagenSuite extends TrimDatagenSuite {
    public TestTrimDatagenSuite(GatherDataEvent event, String modid, @Nullable BiConsumer<String, String> translationConsumer) {
        super(event, modid, translationConsumer);
        testgen();
    }

    public void testgen() {
        makePattern(ResourceKey.create(Registries.TRIM_PATTERN, TrimmedTest.id("testertester")), ModItems.SCANNER.get(), patternConfig -> patternConfig.createCopyRecipe(Items.IRON_BOOTS));
    }
}
