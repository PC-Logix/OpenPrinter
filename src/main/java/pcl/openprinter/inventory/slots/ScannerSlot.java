package pcl.openprinter.inventory.slots;

import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemWritableBook;
import net.minecraft.item.ItemWrittenBook;

public class ScannerSlot extends PrintedPaperSlot {
    public ScannerSlot(IInventory par1iInventory, int par2, int par3, int par4) {
        super(par1iInventory, par2, par3, par4);
    }

    public boolean isItemValid(ItemStack itemstack) {
        if (itemstack.getItem() instanceof ItemWritableBook)
            return true;
        if (itemstack.getItem() instanceof ItemWrittenBook)
            return true;
        if (itemstack.getItem().getRegistryName().toString().equals("bibliocraft:bigbook"))
            return true;
        else
            return super.isItemValid(itemstack);
    }
}
