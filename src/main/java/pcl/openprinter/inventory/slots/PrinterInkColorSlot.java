package pcl.openprinter.inventory.slots;

import pcl.openprinter.items.PrinterInkColor;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PrinterInkColorSlot extends Slot {

	public PrinterInkColorSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		// TODO Auto-generated constructor stub
	}
	
    public boolean isItemValid(ItemStack itemstack) {
        return itemstack.getItem() instanceof PrinterInkColor;
    }

}
