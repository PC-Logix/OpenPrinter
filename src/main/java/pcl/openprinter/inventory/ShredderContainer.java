package pcl.openprinter.inventory;

import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.minecraft.entity.player.InventoryPlayer;
import pcl.openprinter.tileentity.ShredderTE;

public class ShredderContainer extends CustomContainer {
	protected ShredderTE tileEntity;


	public ShredderContainer (InventoryPlayer inventoryPlayer, ShredderTE tileEntity2){
		tileEntity = tileEntity2;
		//Blank Paper
		addSlotToContainer(new SlotItemHandler(tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP), 0, 79, 34));

		//Output slots
		IItemHandler inventoryOutput = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
		for (int i = 0; i < 9; i++) {
			addSlotToContainer(new SlotItemHandler(inventoryOutput, i, 44 + i * 18 - 36, 87));
		}

		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer, 8, 114);
	}


}
