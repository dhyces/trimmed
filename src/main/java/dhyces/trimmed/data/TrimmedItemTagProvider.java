package dhyces.trimmed.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricTagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Items;

import java.util.concurrent.CompletableFuture;

public class TrimmedItemTagProvider extends FabricTagProvider.ItemTagProvider {
    public TrimmedItemTagProvider(FabricDataOutput output, CompletableFuture<HolderLookup.Provider> completableFuture) {
        super(output, completableFuture);
    }

    @Override
    protected void addTags(HolderLookup.Provider arg) {
        getOrCreateTagBuilder(ItemTags.TRIM_MATERIALS)
                .add(Items.ECHO_SHARD)
                .add(Items.NAUTILUS_SHELL)
                .add(Items.BLAZE_POWDER)
                .add(Items.PRISMARINE_CRYSTALS)
                .add(Items.GLOW_INK_SAC);
    }
}
