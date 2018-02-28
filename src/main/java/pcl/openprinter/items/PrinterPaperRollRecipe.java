package pcl.openprinter.items;

/*
 * @Author Justin Aquadro
 */

import net.minecraft.init.Items;
import net.minecraft.inventory.InventoryCrafting;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.NonNullList;
import net.minecraft.world.World;

import net.minecraftforge.registries.IForgeRegistryEntry;

import pcl.openprinter.ContentRegistry;

public class PrinterPaperRollRecipe extends IForgeRegistryEntry.Impl<IRecipe> implements IRecipe
{
    @Override
    public boolean matches (InventoryCrafting inventory, World world) {
        if (inventory.getSizeInventory() >= 9) {
            ItemStack stack00 = inventory.getStackInRowAndColumn(1, 0);
            ItemStack stack01 = inventory.getStackInRowAndColumn(0, 1);
            ItemStack stack10 = inventory.getStackInRowAndColumn(2, 1);
            ItemStack stack11 = inventory.getStackInRowAndColumn(1, 2);

            if (stack00.isEmpty() || stack01.isEmpty() || stack10.isEmpty() || stack11.isEmpty())
                return false;

            int emptyCount = 0;
            for (int i = 0; i < 9; i++) {
                if (inventory.getStackInSlot(i).isEmpty())
                    emptyCount++;
            }

            if (emptyCount != 5)
                return false;

            return stack00.getItem() == Items.PAPER && stack00.getCount() == 64
                && stack01.getItem() == Items.PAPER && stack01.getCount() == 64
                && stack10.getItem() == Items.PAPER && stack10.getCount() == 64
                && stack11.getItem() == Items.PAPER && stack11.getCount() == 64;
        }
        else
            return false;
    }

    @Override
    public ItemStack getCraftingResult (InventoryCrafting inventory) {
        return new ItemStack(ContentRegistry.printerPaperRoll, 1);
    }
    
    @Override
    public boolean canFit(int width, int height) {
        return width >= 3 && height >= 3;
    }

    @Override
    public ItemStack getRecipeOutput () {
        return new ItemStack(ContentRegistry.printerPaperRoll, 1);
    }

	@Override
	public NonNullList<ItemStack> getRemainingItems(InventoryCrafting inv) {
		return NonNullList.create();
	}
}
