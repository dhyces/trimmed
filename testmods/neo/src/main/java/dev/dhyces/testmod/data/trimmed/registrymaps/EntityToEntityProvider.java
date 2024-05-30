package dev.dhyces.testmod.data.trimmed.registrymaps;

import dev.dhyces.testmod.TrimmedTest;
import dev.dhyces.testmod.client.TestClientMapKeys;
import dev.dhyces.trimmed.api.data.map.ClientRegistryMapDataProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.world.entity.EntityType;
import net.neoforged.neoforge.common.data.ExistingFileHelper;

import java.util.concurrent.CompletableFuture;

public class EntityToEntityProvider extends ClientRegistryMapDataProvider<EntityType<?>> {
    public EntityToEntityProvider(PackOutput packOutput, CompletableFuture<HolderLookup.Provider> lookupProviderFuture, ExistingFileHelper existingFileHelper) {
        super(packOutput, TrimmedTest.MODID, lookupProviderFuture, Registries.ENTITY_TYPE, existingFileHelper);
    }

    @Override
    protected void addMaps(HolderLookup.Provider lookupProvider) {
        map(TestClientMapKeys.DATAGEN_ENTITY_TRANSFORM, lookupProvider)
                .put(EntityType.ALLAY, EntityType.BAT);
    }
}
