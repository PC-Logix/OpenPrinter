package pcl.openprinter.inventory;

import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nonnull;

public class BriefcaseInventory extends ItemStackHandler {
    public BriefcaseInventory(int slotCount){
        super(slotCount);
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack){
        return !(stack.getItem() instanceof ItemBlock);
    }

}
