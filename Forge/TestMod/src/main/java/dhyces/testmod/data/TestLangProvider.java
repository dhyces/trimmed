package dhyces.testmod.data;

import dhyces.testmod.TrimmedTest;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class TestLangProvider extends LanguageProvider {


    public TestLangProvider(PackOutput output) {
        super(output, TrimmedTest.MODID, "en_us");
    }

    @Override
    public void addTranslations() {
        add("trimmed.trim_material.echo", "Echo Material");
        add("trimmed.trim_material.blaze", "Blaze Material");
        add("trimmed.trim_material.shell", "Shell Material");
        add("trimmed.trim_material.prismarine", "Prismarine Material");
        add("trimmed.trim_material.glow", "Glow Material");
        add("trimmed.trim_material.adamantium", "Adamantium Material");

    }
}
