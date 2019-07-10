package pcl.openprinter.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PrinterOutputSlot extends Slot {

    public PrinterOutputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
        // TODO Auto-generated constructor stub
    }

    // even if some people may do that, we dont want players to put stuff in the output slot :P
    public boolean isItemValid(ItemStack itemstack) {
        return false;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }

}