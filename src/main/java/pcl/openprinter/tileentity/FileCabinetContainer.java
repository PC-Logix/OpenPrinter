package pcl.openprinter.tileentity;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.FileCabinetSlot;
import pcl.openprinter.items.FolderInventory;
import pcl.openprinter.items.PrintedPage;
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
public class FileCabinetContainer extends Container{
	protected FileCabinetTE tileEntity;
	private List<Slot> outputSlots;
	private List<Slot> playerSlots;
	private List<Slot> hotbarSlots;

	private static final int INV_START = 27, INV_END = INV_START+26,
			HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;
	
	public FileCabinetContainer (InventoryPlayer inventoryPlayer, FileCabinetTE tileEntity){
		//Output slots
		outputSlots = new ArrayList<Slot>();
		for (int i = 1; i < 10; i++) {
			outputSlots.add(addSlotToContainer(new FileCabinetSlot(tileEntity, i, 44 + i * 18 - 54, 15)));
			outputSlots.add(addSlotToContainer(new FileCabinetSlot(tileEntity, i + 9, 44 + i * 18 - 54, 33)));
			outputSlots.add(addSlotToContainer(new FileCabinetSlot(tileEntity, i + 18, 44 + i * 18 - 54, 51)));
		}

		//commonly used vanilla code that adds the player's inventory
		bindPlayerInventory(inventoryPlayer);
	}

	@Override
	public boolean canInteractWith(EntityPlayer player) {
		return true;
	}


	protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
		playerSlots = new ArrayList<Slot>();
		for (int i = 0; i < 3; i++) {
			for (int j = 0; j < 9; j++) {
				playerSlots.add(addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 78 + i * 18)));
			}
		}

		hotbarSlots = new ArrayList<Slot>();
		for (int i = 0; i < 9; i++) {
			hotbarSlots.add(addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 136)));
		}
	}

	@Override
	public ItemStack transferStackInSlot(EntityPlayer par1EntityPlayer, int index)
	{
		ItemStack itemstack = null;
		Slot slot = (Slot) this.inventorySlots.get(index);

		if (slot != null && slot.getHasStack()) {
			ItemStack itemstack1 = slot.getStack();
			itemstack = itemstack1.copy();

			// If item is in our custom Inventory or armor slot
			if (index < INV_START) {
				// try to place in player inventory / action bar
				if (!this.mergeItemStack(itemstack1, INV_START, HOTBAR_END+1, true))
				{
					return null;
				}

				slot.onSlotChange(itemstack1, itemstack);
			} else {
				//if (itemstack1.getItem() instanceof PrintedPage) {
					if (!this.mergeItemStack(itemstack1, 0, 27, false)) {
						return null;
					}
				//}
			}

			if (itemstack1.stackSize == 0) {
				slot.putStack((ItemStack) null);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.stackSize == itemstack.stackSize) {
				return null;
			}

			slot.onPickupFromSlot(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}

	@Override
	protected boolean mergeItemStack(ItemStack is, int slotStart, int slotFinish, boolean par4)
	{
		boolean merged = false;
		int slotIndex = slotStart;

		if (par4)
			slotIndex = slotFinish - 1;

		Slot slot;
		ItemStack slotstack;

		if (is.isStackable())
		{
			while (is.stackSize > 0 && (!par4 && slotIndex < slotFinish || par4 && slotIndex >= slotStart))
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				slotstack = slot.getStack();

				if (slotstack != null
						&& slotstack.getItem() == is.getItem()
						//&& !is.getHasSubtypes()
						&& is.getItemDamage() == slotstack.getItemDamage()
						&& ItemStack.areItemStackTagsEqual(is, slotstack)
						&& slotstack.stackSize < slot.getSlotStackLimit())
				{
					int mergedStackSize = is.stackSize + getSmaller(slotstack.stackSize, slot.getSlotStackLimit());

					//First we check if we can add the two stacks together and the resulting stack is smaller than the maximum size for the slot or the stack
					if (mergedStackSize <= is.getMaxStackSize() && mergedStackSize <= slot.getSlotStackLimit())
					{
						is.stackSize = 0;
						slotstack.stackSize = mergedStackSize;
						slot.onSlotChanged();
						merged = true;
					}
					else if (slotstack.stackSize < is.getMaxStackSize() && slotstack.stackSize < slot.getSlotStackLimit())
					{
						// Slot stack size is greater than or equal to the item's max stack size. Most containers are this case.
						if (slot.getSlotStackLimit() >= is.getMaxStackSize())
						{
							is.stackSize -= is.getMaxStackSize() - slotstack.stackSize;
							slotstack.stackSize = is.getMaxStackSize();
							slot.onSlotChanged();
							merged = true;
						}
						// Slot stack size is smaller than the item's normal max stack size. Example: Log Piles
						else if (slot.getSlotStackLimit() < is.getMaxStackSize())
						{
							is.stackSize -= slot.getSlotStackLimit() - slotstack.stackSize;
							slotstack.stackSize = slot.getSlotStackLimit();
							slot.onSlotChanged();
							merged = true;
						}
					}
				}

				if (par4)
					--slotIndex;
				else
					++slotIndex;
			}
		}

		if (is.stackSize > 0)
		{
			if (par4)
				slotIndex = slotFinish - 1;
			else
				slotIndex = slotStart;

			while (!par4 && slotIndex < slotFinish || par4 && slotIndex >= slotStart)
			{
				slot = (Slot)this.inventorySlots.get(slotIndex);
				slotstack = slot.getStack();
				if (slotstack == null && slot.isItemValid(is) && slot.getSlotStackLimit() < is.stackSize)
				{
					ItemStack copy = is.copy();
					copy.stackSize = slot.getSlotStackLimit();
					is.stackSize -= slot.getSlotStackLimit();
					slot.putStack(copy);
					slot.onSlotChanged();
					merged = true;
					//this.bagsSlotNum = slotIndex;
					break;
				}
				else if (slotstack == null && slot.isItemValid(is))
				{
					slot.putStack(is.copy());
					slot.onSlotChanged();
					is.stackSize = 0;
					merged = true;
					break;
				}

				if (par4)
					--slotIndex;
				else
					++slotIndex;
			}
		}

		return merged;
	}

	protected int getSmaller(int i, int j)
	{
		if(i < j)
			return i;
		else
			return j;
	}
	
}
