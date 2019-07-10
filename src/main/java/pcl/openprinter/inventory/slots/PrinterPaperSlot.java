package pcl.openprinter.inventory.slots;

import pcl.openprinter.items.PrinterPaperRoll;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PrinterPaperSlot extends Slot {

	public PrinterPaperSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public boolean isItemValid(ItemStack itemstack) {
        if (itemstack.getItem() instanceof PrinterPaperRoll) {
            return true;
        }
        else if (itemstack.getItem().equals(Items.PAPER)) {
            return true;
        }
        else if (itemstack.getItem().equals(Items.NAME_TAG)) {
            return true;
        }
        return false;
    }

    @Override
    public int getSlotStackLimit()
    {
        return 1;
    }
}
