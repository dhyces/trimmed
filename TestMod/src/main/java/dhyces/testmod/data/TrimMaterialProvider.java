package dhyces.testmod.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;

public class TrimMaterialProvider extends FabricDynamicRegistryProvider {

    public TrimMaterialProvider(CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture, FabricDataOutput output, FabricDataGenerator.Pack pack, String modid) {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries) {

    }

    @Override
    public CompletableFuture<?> run(DataWriter cachedOutput) {
        return null;
    }

    @Override
    public String getName() {
        return "Trims";
    }
}
