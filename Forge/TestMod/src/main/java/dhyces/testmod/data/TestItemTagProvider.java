package dhyces.testmod.data;

import dhyces.testmod.ModItems;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.tags.IntrinsicHolderTagsProvider;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.item.Item;
import net.minecraftforge.common.data.ExistingFileHelper;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.CompletableFuture;

public class TestItemTagProvider extends IntrinsicHolderTagsProvider<Item> {


    public TestItemTagProvider(PackOutput p_256164_, CompletableFuture<HolderLookup.Provider> p_256488_, String modId, @Nullable ExistingFileHelper existingFileHelper) {
        super(p_256164_, Registries.ITEM, p_256488_, item -> item.builtInRegistryHolder().key(), modId, existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider pProvider) {
        tag(ItemTags.TRIMMABLE_ARMOR)
                .add(
                        ModItems.ADAMANTIUM_HELMET.get(),
                        ModItems.ADAMANTIUM_CHESTPLATE.get(),
                        ModItems.ADAMANTIUM_LEGGINGS.get(),
                        ModItems.ADAMANTIUM_BOOTS.get()
                );
    }
}
