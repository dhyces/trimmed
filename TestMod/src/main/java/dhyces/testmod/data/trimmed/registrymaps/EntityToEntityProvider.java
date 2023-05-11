package dhyces.testmod.data.trimmed.registrymaps;

import dhyces.testmod.TestClientMaps;
import dhyces.testmod.TrimmedTest;
import dhyces.trimmed.api.data.maps.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.minecraftforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class EntityToEntityProvider extends ClientRegistryMapDataProvider<EntityType<?>> {
    public EntityToEntityProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, Registries.ENTITY_TYPE, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        registryAwareWithMapper(TestClientMaps.DATAGEN_ENTITY_TRANSFORM, EntityType.class, entityType -> entityType.builtInRegistryHolder().key().location().toString(), lookupProvider)
                .put(EntityType.ALLAY, EntityType.BAT);
    }
}
