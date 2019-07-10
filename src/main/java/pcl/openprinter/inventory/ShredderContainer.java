package pcl.openprinter.inventory;

import pcl.openprinter.inventory.slots.ShredderInputSlot;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pcl.openprinter.tileentity.ShredderTE;


/**
 * @author Caitlyn
 *
 */
public class ShredderContainer extends CustomContainer {
	protected ShredderTE tileEntity;


	public ShredderContainer (InventoryPlayer inventoryPlayer, ShredderTE tileEntity2){
		tileEntity = tileEntity2;
		//Blank Paper
		addSlotToContainer(new ShredderInputSlot(tileEntity, 0, 79, 34));

		//Output slots
		for (int i = 1; i < 10; i++) {
			//outputSlots.add(addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18 - 54, 69)));
			addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18 - 54, 87));
		}

		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {

		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 15 + 15));
			}
		}

		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142 + 15 + 15));
		}
	}

}
