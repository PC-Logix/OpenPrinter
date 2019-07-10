package pcl.openprinter.inventory.slots;

import pcl.openprinter.items.PrinterInkBlack;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PrinterInkBlackSlot extends Slot {

	public PrinterInkBlackSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
    public boolean isItemValid(ItemStack itemstack){
	    return itemstack.getItem() instanceof PrinterInkBlack;
    }

}
