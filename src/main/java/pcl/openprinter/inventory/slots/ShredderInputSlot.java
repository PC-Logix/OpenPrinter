package pcl.openprinter.inventory.slots;

import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import pcl.openprinter.items.PrintedPage;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ShredderInputSlot extends SlotItemHandler {

	public ShredderInputSlot(IItemHandler inventoryIn, int index, int xPosition, int yPosition) {
		super(inventoryIn, index, xPosition, yPosition);
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
