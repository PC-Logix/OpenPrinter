package pcl.openprinter.gui;

import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterPaperRoll;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class ShredderInputSlot extends Slot {

	public ShredderInputSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
    public boolean isItemValid(ItemStack itemstack)
    {

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
    /**
     * Called when the player picks up an item from an inventory slot
     */
    public ItemStack onTake(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
            this.onCrafting(par2ItemStack);
            return super.onTake(par1EntityPlayer, par2ItemStack);
    }
}
