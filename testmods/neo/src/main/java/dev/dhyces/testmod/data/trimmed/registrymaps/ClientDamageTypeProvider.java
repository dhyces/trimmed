package dev.dhyces.testmod.data.trimmed.registrymaps;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.trimmed.api.data.map.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ClientDamageTypeProvider extends ClientRegistryMapDataProvider<DamageType> {
    public ClientDamageTypeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, Registries.DAMAGE_TYPE, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        // TODO: Bring back with data pack support
//        map(TestClientMapKeys.DATAGEN_TEST_DAMAGE_TYPE_MAP, lookupProvider).put(DamageTypes.DRAGON_BREATH, 4);

    }
}
