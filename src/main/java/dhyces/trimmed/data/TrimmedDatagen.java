package dhyces.trimmed.data;

import dhyces.trimmed.ModTrimMaterials;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.registries.RegistriesDatapackGenerator;

import java.util.concurrent.CompletableFuture;

public class TrimmedDatagen implements DataGeneratorEntrypoint {

    public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder().add(Registries.TRIM_MATERIAL, ModTrimMaterials::bootstrap);

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator fabricDataGenerator) {
        FabricDataGenerator.Pack pack = fabricDataGenerator.createPack();
        CompletableFuture<HolderLookup.Provider> future = CompletableFuture.supplyAsync(() -> BUILDER.build(RegistryAccess.fromRegistryOfRegistries(BuiltInRegistries.REGISTRY)), Util.backgroundExecutor());
        pack.addProvider((FabricDataGenerator.Pack.Factory<RegistriesDatapackGenerator>) packOutput -> new RegistriesDatapackGenerator(packOutput, future));
        pack.addProvider(TrimmedItemTagProvider::new);

        pack.addProvider((FabricDataGenerator.Pack.Factory<TrimmedAtlasProvider>) TrimmedAtlasProvider::new);
        pack.addProvider(TrimmedLangProvider::new);
    }
}
