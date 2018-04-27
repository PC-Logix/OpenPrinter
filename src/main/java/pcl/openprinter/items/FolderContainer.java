package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.PrintedPaperSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.ClickType;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class FolderContainer extends Container {
	/** The Item Inventory for this Container, only needed if you want to reference isUsableByPlayer */
	public final FolderInventory inventory;

	/** Using these will make transferStackInSlot easier to understand and implement
	 * INV_START is the index of the first slot in the Player's Inventory, so our
	 * InventoryItem's number of slots (e.g. 5 slots is array indices 0-4, so start at 5)
	 * Notice how we don't have to remember how many slots we made? We can just use
	 * InventoryItem.INV_SIZE and if we ever change it, the Container updates automatically. */
	private static final int INV_START = FolderInventory.INV_SIZE, INV_END = INV_START+26,
			HOTBAR_START = INV_END+1, HOTBAR_END = HOTBAR_START+8;

	// If you're planning to add armor slots, put those first like this:
	// ARMOR_START = InventoryItem.INV_SIZE, ARMOR_END = ARMOR_START+3,
	// INV_START = ARMOR_END+1, and then carry on like above.

	public FolderContainer(EntityPlayer par1Player, InventoryPlayer inventoryPlayer, FolderInventory inventoryItem)
	{
		this.inventory = inventoryItem;

		int i;

		// ITEM INVENTORY - you'll need to adjust the slot locations to match your texture file
		// I have them set vertically in columns of 4 to the right of the player model
		for (i = 0; i < FolderInventory.INV_SIZE; ++i)
		{
			this.addSlotToContainer(new PrintedPaperSlot(this.inventory, i, 8 + i * 18, 19));
		}

		// PLAYER INVENTORY - uses default locations for standard inventory texture file
		for (i = 0; i < 3; ++i)
		{
			for (int j = 0; j < 9; ++j)
			{
				this.addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 55 + i * 18));
			}
		}

		// PLAYER ACTION BAR - uses default locations for standard action bar texture file
		for (i = 0; i < 9; ++i)
		{
			this.addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 113));
		}
	}

	@Override
	public boolean canInteractWith(EntityPlayer entityplayer)
	{
		// be sure to return the inventory's isUsableByPlayer method
		// if you defined special behavior there:
		return inventory.isUsableByPlayer(entityplayer);
	}

	/**
	 * Called when a player shift-clicks on a slot. You must override this or you will crash when someone does that.
	 */
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
				if (itemstack1.getItem() instanceof PrintedPage && !this.mergeItemStack(itemstack1, 0, FolderInventory.INV_SIZE, false)) {
					return null;
				}

			}

			if (itemstack1.getCount() == 0) {
				slot.putStack(ItemStack.EMPTY);
			} else {
				slot.onSlotChanged();
			}

			if (itemstack1.getCount() == itemstack.getCount()) {
				return null;
			}

			slot.onTake(par1EntityPlayer, itemstack1);
		}

		return itemstack;
	}

	/**
	 * You should override this method to prevent the player from moving the stack that
	 * opened the inventory, otherwise if the player moves it, the inventory will not
	 * be able to save properly
	 */
	@Override
	public ItemStack slotClick(int slot, int button, ClickType clickTypeIn, EntityPlayer player) {
		// this will prevent the player from interacting with the item that opened the inventory:
		if (slot >= 0 && getSlot(slot) != null && getSlot(slot).getStack() == player.getHeldItemMainhand()) {
			return ItemStack.EMPTY;
		}
		return super.slotClick(slot, button, clickTypeIn, player);
	}
}