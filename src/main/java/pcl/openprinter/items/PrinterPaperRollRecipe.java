package pcl.openprinter.items;

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;

public class PrinterPaperRollRecipe implements IRecipe
{
    @Override
    public boolean matches (InventoryCrafting inventory, World world) {
        if (inventory.getSizeInventory() >= 9)
            return matches(inventory, 0, 0)
                || matches(inventory, 1, 0)
                || matches(inventory, 0, 1)
                || matches(inventory, 1, 1);
        else if (inventory.getSizeInventory() >= 4)
            return matches(inventory, 0, 0);
        else
            return false;
    }

    private boolean matches (InventoryCrafting inventory, int offsetX, int offsetY) {
        ItemStack stack00 = inventory.getStackInRowAndColumn(0 + offsetX, 0 + offsetY);
        ItemStack stack01 = inventory.getStackInRowAndColumn(1 + offsetX, 0 + offsetY);
        ItemStack stack10 = inventory.getStackInRowAndColumn(0 + offsetX, 1 + offsetY);
        ItemStack stack11 = inventory.getStackInRowAndColumn(1 + offsetX, 1 + offsetY);

        if (stack00 == null || stack01 == null || stack10 == null || stack11 == null)
            return false;

        int emptyCount = 0;
        for (int i = 0; i < 9; i++) {
            if (inventory.getStackInSlot(i) == null)
                emptyCount++;
        }

        if (emptyCount != 5)
            return false;

        return stack00.getItem() == Items.paper && stack00.stackSize == 64
            && stack01.getItem() == Items.paper && stack01.stackSize == 64
            && stack10.getItem() == Items.paper && stack10.stackSize == 64
            && stack11.getItem() == Items.paper && stack11.stackSize == 64;
    }

    @Override
    public ItemStack getCraftingResult (InventoryCrafting inventory) {
        return new ItemStack(OpenPrinter.printerPaperRoll, 1);
    }

    @Override
    public int getRecipeSize () {
        return 9;
    }

    @Override
    public ItemStack getRecipeOutput () {
        return new ItemStack(OpenPrinter.printerPaperRoll, 1);
    }
}
