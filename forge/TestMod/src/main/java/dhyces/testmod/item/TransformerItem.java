package dhyces.testmod.item;

import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class TransformerItem extends Item {
    public TransformerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        // TODO: impl when datapack maps are implemented. I forgot they weren't yet...
        return super.useOn(pContext);
    }
}
