package pcl.openprinter.inventory;

import net.minecraft.item.ItemStack;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;

public class FolderContainer extends CustomContainer {
	public ItemStack folderStack;
	public IItemHandler inventory;

	public FolderContainer(EntityPlayer player, InventoryPlayer inventoryPlayer){
		folderStack = player.getHeldItemMainhand();

		inventory = new FolderInventory(folderStack);

	    for (int i = 0; i < inventory.getSlots(); i++)
			this.addSlotToContainer(new SlotItemHandler(inventory, i, 8 + i * 18, 19));

		bindPlayerInventory(inventoryPlayer, 8, 55);
	}

}