package dev.dhyces.testmod.data.trimmed.registrymaps;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.trimmed.api.data.map.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ClientBlockProvider extends ClientRegistryMapDataProvider<Block> {
    public ClientBlockProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, Registries.BLOCK, lookupProviderFuture, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        map(TestClientMapKeys.DATAGEN_TEST_BLOCK_MAP, lookupProvider).put(Blocks.FIRE, "Hello!");
    }
}
