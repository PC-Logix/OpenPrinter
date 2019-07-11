package pcl.openprinter.inventory;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.SlotItemHandler;
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
		addSlotToContainer(new ShredderInputSlot(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), 0, 79, 34));

		//Output slots
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotItemHandler(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN), i, 44 + i * 18 - 36, 87));
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
