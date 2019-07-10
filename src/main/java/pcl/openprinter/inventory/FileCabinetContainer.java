package pcl.openprinter.inventory;

import pcl.openprinter.inventory.slots.FileCabinetSlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pcl.openprinter.tileentity.FileCabinetTE;

import javax.annotation.Nonnull;

/**
 * @author Caitlyn
 *
 */
public class FileCabinetContainer extends CustomContainer {
	protected FileCabinetTE tileEntity;
	
	public FileCabinetContainer (InventoryPlayer inventoryPlayer, FileCabinetTE tileEntity){
		//Output slots
		for (int i = 1; i < 10; i++) {
			addSlotToContainer(new FileCabinetSlot(tileEntity, i, 44 + i * 18 - 54, 15));
			addSlotToContainer(new FileCabinetSlot(tileEntity, i + 9, 44 + i * 18 - 54, 33));
			addSlotToContainer(new FileCabinetSlot(tileEntity, i + 18, 44 + i * 18 - 54, 51));
		}

		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 78 + i * 18));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 136));
		}
	}
	
}
