package pcl.openprinter.inventory.slots;

import pcl.openprinter.items.PrintedPage;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ShredderInputSlot extends Slot {

	public ShredderInputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
    public boolean isItemValid(ItemStack itemstack) {
        if (itemstack.getItem() instanceof PrintedPage || itemstack.getItem().equals(Items.BOOK) || itemstack.getItem().equals(Items.PAPER) || itemstack.getItem().equals(Items.WRITTEN_BOOK) || itemstack.getItem().equals(Items.WRITABLE_BOOK))
        {
            return true;
        }
        else if (itemstack.getItem().equals(Items.PAPER)) {
            return true;
        }
        else if (itemstack.getItem().equals(Items.BOOK)) {
            return true;
        }
        return false;
    }
}
