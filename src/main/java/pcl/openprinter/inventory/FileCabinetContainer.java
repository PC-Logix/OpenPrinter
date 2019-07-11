package pcl.openprinter.inventory;

import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.entity.player.InventoryPlayer;
import pcl.openprinter.tileentity.FileCabinetTE;

public class FileCabinetContainer extends CustomContainer {
	public FileCabinetContainer (InventoryPlayer inventoryPlayer, FileCabinetTE tileEntity){
		IItemHandler inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

		//Output slots
		for (int i = 1; i < 10; i++) {
			addSlotToContainer(new SlotItemHandler(inventory, i, 44 + i * 18 - 54, 15));
			addSlotToContainer(new SlotItemHandler(inventory, i + 9, 44 + i * 18 - 54, 33));
			addSlotToContainer(new SlotItemHandler(inventory, i + 18, 44 + i * 18 - 54, 51));
		}

		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer, 8, 78);
	}
}
