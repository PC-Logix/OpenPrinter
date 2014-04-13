package pcl.openprinter.gui;

import pcl.openprinter.Config;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.items.PrinterPaper;
import pcl.openprinter.items.PrinterPaperRoll;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.Configuration;

public class PrinterPaperSlot extends Slot {

	public PrinterPaperSlot(IInventory par1iInventory, int par2, int par3, int par4) {
		super(par1iInventory, par2, par3, par4);
		// TODO Auto-generated constructor stub
	}
	
    public boolean isItemValid(ItemStack itemstack)
    {

            if (itemstack.getItem() instanceof PrinterPaperRoll)
            {
            	return true;
            } 
            else if (itemstack.getItem() instanceof PrinterPaper) {
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
