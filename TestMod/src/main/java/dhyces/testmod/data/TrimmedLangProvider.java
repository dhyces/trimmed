package dhyces.testmod.data;

import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.LanguageProvider;

public class TrimmedLangProvider extends LanguageProvider {


    public TrimmedLangProvider(PackOutput output) {
        super(output, "testmod", "en_us");
    }

    @Override
    public void addTranslations() {
        add("trimmed.trim_material.echo", "Echo");
        add("trimmed.trim_material.blaze", "Blaze");
        add("trimmed.trim_material.shell", "Shell");
        add("trimmed.trim_material.prismarine", "Prismarine");
        add("trimmed.trim_material.glow", "Glow");

    }
}
