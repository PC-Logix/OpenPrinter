package pcl.openprinter.inventory.slots;

import pcl.openprinter.items.PrintedPage;
import net.minecraft.item.ItemStack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;

public class PrintedPaperSlot extends Slot {

	public PrintedPaperSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		// TODO Auto-generated constructor stub
	}
	
    public boolean isItemValid(ItemStack itemstack) {
	    return itemstack.getItem() instanceof PrintedPage;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
    
}
