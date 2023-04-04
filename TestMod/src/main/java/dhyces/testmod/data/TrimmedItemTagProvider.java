package dhyces.testmod.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.item.Items;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.registry.tag.ItemTags;
import java.util.concurrent.CompletableFuture;

public class TrimmedItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public TrimmedItemTagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup arg) {
        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(Items.ECHO_SHARD)
                .add(Items.NAUTILUS_SHELL)
                .add(Items.BLAZE_POWDER)
                .add(Items.PRISMARINE_CRYSTALS)
                .add(Items.GLOW_INK_SAC);
    }
}
