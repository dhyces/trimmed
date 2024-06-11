package dev.dhyces.testmod.data.trimmed.registrytags;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientTags;
import dev.dhyces.trimmed.api.data.tag.ClientRegistryTagDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.Biomes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class BiomeClientTagProvider extends ClientRegistryTagDataProvider<Biome> {
    public BiomeClientTagProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, Registries.BIOME, lookupProviderFuture, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(TestClientTags.TEST_BIOME_TAG, provider).add(Biomes.BEACH);
    }
}
