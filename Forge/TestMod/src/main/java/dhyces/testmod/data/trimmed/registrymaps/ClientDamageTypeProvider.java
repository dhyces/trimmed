package dhyces.testmod.data.trimmed.registrymaps;

import dhyces.testmod.TestClientMaps;
import dhyces.testmod.TrimmedTest;
import dev.dhyces.trimmed.api.data.maps.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.damagesource.DamageTypes;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class ClientDamageTypeProvider extends ClientRegistryMapDataProvider<DamageType> {
    public ClientDamageTypeProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, Registries.DAMAGE_TYPE, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        mapWithMapper(TestClientMaps.DATAGEN_TEST_DAMAGE_TYPE_MAP, (Integer i) -> i.toString()).put(DamageTypes.DRAGON_BREATH, 4);

    }
}
