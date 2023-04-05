package dhyces.testmod.data;

import dhyces.testmod.ModTrimMaterials;
import dhyces.testmod.ModTrimPatterns;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.data.report.DynamicRegistriesProvider;
import net.minecraft.item.trim.ArmorTrimMaterial;
import net.minecraft.item.trim.ArmorTrimPattern;
import net.minecraft.registry.DynamicRegistryManager;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.Registries;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.RegistryWrapper;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public class TrimmedDatagen implements DataGeneratorEntrypoint {

    private static final RegistryBuilder BUILDER = new RegistryBuilder()
            .addRegistry(RegistryKeys.TRIM_MATERIAL, TrimmedDatagen::bootstrapMaterials)
            .addRegistry(RegistryKeys.TRIM_PATTERN, TrimmedDatagen::bootstrapPatterns);

    private static final Map<RegistryKey<ArmorTrimMaterial>, ArmorTrimMaterial> MATERIALS = new HashMap<>();
    private static final Map<RegistryKey<ArmorTrimPattern>, ArmorTrimPattern> PATTERNS = new HashMap<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        CompletableFuture<RegistryWrapper.WrapperLookup> future = CompletableFuture.supplyAsync(() -> BUILDER.createWrapperLookup(DynamicRegistryManager.of(Registries.REGISTRIES)));
        pack.addProvider((FabricDataGenerator.Pack.Factory<DynamicRegistriesProvider>) packOutput -> new DynamicRegistriesProvider(packOutput, future));
        pack.addProvider(TrimmedItemTagProvider::new);

        pack.addProvider((FabricDataGenerator.Pack.Factory<TrimmedAtlasProvider>) TrimmedAtlasProvider::new);
        pack.addProvider(TrimmedLangProvider::new);
        pack.addProvider(TrimmedModelProvider::new);
    }

    private static void bootstrapMaterials(Registerable<ArmorTrimMaterial> context) {
        for (Map.Entry<RegistryKey<ArmorTrimMaterial>, ArmorTrimMaterial> entry : MATERIALS.entrySet()) {
            context.register(entry.getKey(), entry.getValue());
        }
        ModTrimMaterials.bootstrap(context);
    }

    private static void bootstrapPatterns(Registerable<ArmorTrimPattern> context) {
        for (Map.Entry<RegistryKey<ArmorTrimPattern>, ArmorTrimPattern> entry : PATTERNS.entrySet()) {
            context.register(entry.getKey(), entry.getValue());
        }
        ModTrimPatterns.bootstrap(context);
    }
}
