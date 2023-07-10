package dhyces.testmod.item;

import dhyces.testmod.TrimmedTestClient;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.context.UseOnContext;

public class ScannerItem extends Item {
    public ScannerItem(Properties pProperties) {
        super(pProperties);
    }

    @Override
    public InteractionResult useOn(UseOnContext pContext) {
        if (pContext.getLevel().isClientSide()) {
            TrimmedTestClient.printDescriptor(pContext.getPlayer(), pContext.getLevel().getBlockState(pContext.getClickedPos()).getBlock());
        }
        return InteractionResult.sidedSuccess(pContext.getLevel().isClientSide());
    }
}
