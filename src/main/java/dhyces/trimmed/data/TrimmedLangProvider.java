package dhyces.trimmed.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class TrimmedLangProvider extends FabricLanguageProvider {

    protected TrimmedLangProvider(FabricDataOutput dataOutput) {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder translationBuilder) {
        translationBuilder.add("trimmed.trim_material.echo", "Echo");
        translationBuilder.add("trimmed.trim_material.blaze", "Blaze");
        translationBuilder.add("trimmed.trim_material.shell", "Shell");
        translationBuilder.add("trimmed.trim_material.prismarine", "Prismarine");
        translationBuilder.add("trimmed.trim_material.glow", "Glow");

    }
}
