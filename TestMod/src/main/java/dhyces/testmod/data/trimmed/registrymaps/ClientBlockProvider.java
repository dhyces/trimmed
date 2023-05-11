package dhyces.testmod.data.trimmed.registrymaps;

import dhyces.testmod.TestClientMaps;
import dhyces.testmod.TrimmedTest;
import dhyces.trimmed.api.data.maps.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ClientBlockProvider extends ClientRegistryMapDataProvider<Block> {
    public ClientBlockProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, Registries.BLOCK, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        registryAware(TestClientMaps.DATAGEN_TEST_BLOCK_MAP, lookupProvider).put(Blocks.FIRE, "Hello!");
    }
}
