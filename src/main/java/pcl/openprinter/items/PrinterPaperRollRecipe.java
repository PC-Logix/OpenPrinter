package pcl.openprinter.items;

/*
 * @Author Justin Aquadro
 */

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import pcl.openprinter.ContentRegistry;
import pcl.openprinter.OpenPrinter;

public class PrinterPaperRollRecipe implements IRecipe
{
    @Override
    public boolean matches (InventoryCrafting inventory, World world) {
        if (inventory.getSizeInventory() >= 9) {
            ItemStack stack00 = inventory.getStackInRowAndColumn(1, 0);
            ItemStack stack01 = inventory.getStackInRowAndColumn(0, 1);
            ItemStack stack10 = inventory.getStackInRowAndColumn(2, 1);
            ItemStack stack11 = inventory.getStackInRowAndColumn(1, 2);

            if (stack00 == null || stack01 == null || stack10 == null || stack11 == null)
                return false;

            int emptyCount = 0;
            for (int i = 0; i < 9; i++) {
                if (inventory.getStackInSlot(i) == null)
                    emptyCount++;
            }

            if (emptyCount != 5)
                return false;

            return stack00.getItem() == Items.PAPER && stack00.stackSize == 64
                && stack01.getItem() == Items.PAPER && stack01.stackSize == 64
                && stack10.getItem() == Items.PAPER && stack10.stackSize == 64
                && stack11.getItem() == Items.PAPER && stack11.stackSize == 64;
        }
        else
            return false;
    }

    @Override
    public ItemStack getCraftingResult (InventoryCrafting inventory) {
        return new ItemStack(ContentRegistry.printerPaperRoll, 1);
    }

    @Override
    public int getRecipeSize () {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput () {
        return new ItemStack(ContentRegistry.printerPaperRoll, 1);
    }

	@Override
	public ItemStack[] getRemainingItems(InventoryCrafting inv) {
		// TODO Auto-generated method stub
		return null;
	}
}
