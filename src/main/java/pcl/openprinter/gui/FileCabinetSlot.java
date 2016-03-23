package pcl.openprinter.gui;

import pcl.openprinter.items.ItemFolder;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterPaperRoll;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FileCabinetSlot extends Slot {

	public FileCabinetSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
	}
	
    public boolean isItemValid(ItemStack itemstack)
    {

            if (itemstack.getItem() instanceof PrintedPage || itemstack.getItem().equals(Items.book) || itemstack.getItem().equals(Items.paper) || itemstack.getItem().equals(Items.written_book) || itemstack.getItem().equals(Items.writable_book) || itemstack.getItem() instanceof ItemFolder)
            {
            	return true;
            }
            else if (itemstack.getItem().equals(Items.paper)) {
            	return true;
            }
            else if (itemstack.getItem().equals(Items.book)) {
                return true;
            }
            return false;
    }
    /**
     * Called when the player picks up an item from an inventory slot
     */
    public void onPickupFromSlot(EntityPlayer par1EntityPlayer, ItemStack par2ItemStack)
    {
            this.onCrafting(par2ItemStack);
            super.onPickupFromSlot(par1EntityPlayer, par2ItemStack);
    }
}
