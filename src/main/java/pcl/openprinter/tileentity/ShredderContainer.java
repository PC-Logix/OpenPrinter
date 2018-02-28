package pcl.openprinter.tileentity;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.PrinterPaperSlot;
import pcl.openprinter.gui.ShredderInputSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Caitlyn
 *
 */
public class ShredderContainer extends Container{
	protected ShredderTE tileEntity;

	private Slot paperSlot;
	private List<Slot> specialSlots;
	private List<Slot> outputSlots;
	private List<Slot> playerSlots;
	private List<Slot> hotbarSlots;

	public ShredderContainer (InventoryPlayer inventoryPlayer, ShredderTE tileEntity2){
		tileEntity = tileEntity2;
		//Blank Paper
		paperSlot = addSlotToContainer(new ShredderInputSlot(tileEntity, 0, 79, 34));

		specialSlots = new ArrayList<Slot>();
		specialSlots.add(paperSlot);

		//Output slots
		outputSlots = new ArrayList<Slot>();
		for (int i = 1; i < 10; i++) {
			//outputSlots.add(addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18 - 54, 69)));
			outputSlots.add(addSlotToContainer(new Slot(tileEntity, i, 44 + i * 18 - 54, 87)));
		}

		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return tileEntity.isUsableByPlayer(player);
	}


	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		playerSlots = new ArrayList<Slot>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				playerSlots.add(addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 15 + 15)));
			}
		}

		hotbarSlots = new ArrayList<Slot>();
		for (int i = 0; i < 9; i++) {
			hotbarSlots.add(addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142 + 15 + 15)));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
		ItemStack stack = ItemStack.EMPTY;
		Slot slotObject = (Slot) inventorySlots.get(slot);

		int outputSlotStart = outputSlots.get(0).slotNumber;
		int outputSlotEnd = outputSlots.get(outputSlots.size() - 1).slotNumber + 1;

		// Assume inventory and hotbar slot IDs are contiguous
		int inventoryStart = playerSlots.get(0).slotNumber;
		int hotbarStart = hotbarSlots.get(0).slotNumber;
		int hotbarEnd = hotbarSlots.get(hotbarSlots.size() - 1).slotNumber + 1;

		//null checks and checks if the item can be stacked (maxStackSize > 1)
		if (slotObject != null && slotObject.getHasStack()) {
			ItemStack stackInSlot = slotObject.getStack();
			stack = stackInSlot.copy();

			// Try merge output into inventory and signal change
			if (slot >= outputSlotStart && slot < outputSlotEnd) {
				if (!mergeItemStack(stackInSlot, inventoryStart, hotbarEnd, true))
					return ItemStack.EMPTY;
				slotObject.onSlotChange(stackInSlot, stack);
			}
			// Try merge stacks within inventory and hotbar spaces
			else if (slot >= inventoryStart && slot < hotbarEnd) {
				// If the item is a 'special' item, try putting it into its special slot
				boolean handledSpecialItem = false;
				for (Slot ss : specialSlots) {
					if (!tileEntity.isItemValidForSlot(ss.getSlotIndex(), stackInSlot))
						continue;
					handledSpecialItem = mergeItemStack(stackInSlot, ss.slotNumber, ss.slotNumber + 1, false);
					if (handledSpecialItem)
						break;
				}

				// Else treat it like any normal item
				if (!handledSpecialItem) {
					if (slot >= inventoryStart && slot < hotbarStart) {
						if (!mergeItemStack(stackInSlot, hotbarStart, hotbarEnd, false))
							return ItemStack.EMPTY;
					} else if (slot >= hotbarStart && slot < hotbarEnd && !this.mergeItemStack(stackInSlot, inventoryStart, hotbarStart, false))
						return ItemStack.EMPTY;
				}
			}
			// Try merge stack into inventory
			else if (!mergeItemStack(stackInSlot, inventoryStart, hotbarEnd, false))
				return ItemStack.EMPTY;

			if (stackInSlot.getCount() == 0) {
				slotObject.putStack(ItemStack.EMPTY);
			} else {
				slotObject.onSlotChanged();
			}

			if (stackInSlot.getCount() == stack.getCount()) {
				return ItemStack.EMPTY;
			}
			slotObject.onTake(player, stackInSlot);
		}
		return stack;
	}

}
