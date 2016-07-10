/**
 * 
 */
package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.IChatComponent;
import net.minecraftforge.common.util.Constants;

/**
 * @author Caitlyn
 *
 */
public class FolderInventory implements IInventory {

	private String name = "File Folder";

	/** Provides NBT Tag Compound to reference */
	private final ItemStack invItem;

	/** Defining your inventory size this way is handy */
	public static final int INV_SIZE = 9;

	/** Inventory's size must be same as number of slots you add to the Container class */
	private ItemStack[] inventory = new ItemStack[INV_SIZE];

	private ItemStack stack;
	
	/**
	 * @param itemstack - the ItemStack to which this inventory belongs
	 */
	public FolderInventory(ItemStack stack)
	{
		invItem = stack;
		this.stack = stack;

		// Create a new NBT Tag Compound if one doesn't already exist, or you will crash
		if (!stack.hasTagCompound()) {
			stack.setTagCompound(new NBTTagCompound());
		}
		// note that it's okay to use stack instead of invItem right there
		// both reference the same memory location, so whatever you change using
		// either reference will change in the other

		// Read the inventory contents from NBT
		if(stack.hasDisplayName()) {
			name = stack.getDisplayName();
		}
		readFromNBT(stack.getTagCompound());
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getSizeInventory()
	 */
	@Override
	public int getSizeInventory() {
		return inventory.length;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlot(int)
	 */
	@Override
	public ItemStack getStackInSlot(int slot)
	{
		return inventory[slot];
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#decrStackSize(int, int)
	 */
	@Override
	public ItemStack decrStackSize(int slot, int amount)
	{
		ItemStack stack = getStackInSlot(slot);
		if(stack != null)
		{
			if(stack.stackSize > amount)
			{
				stack = stack.splitStack(amount);
				// Don't forget this line or your inventory will not be saved!
				markDirty();
			}
			else
			{
				// this method also calls onInventoryChanged, so we don't need to call it again
				setInventorySlotContents(slot, null);
			}
		}
		return stack;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getStackInSlotOnClosing(int)
	 */
	@Override
	public ItemStack removeStackFromSlot(int slot)
	{
		ItemStack stack = getStackInSlot(slot);
		setInventorySlotContents(slot, null);
		return stack;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#setInventorySlotContents(int, net.minecraft.item.ItemStack)
	 */
	@Override
	public void setInventorySlotContents(int slot, ItemStack stack)
	{
		inventory[slot] = stack;

		if (stack != null && stack.stackSize > getInventoryStackLimit())
		{
			stack.stackSize = getInventoryStackLimit();
		}

		// Don't forget this line or your inventory will not be saved!
		markDirty();
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInventoryName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#hasCustomInventoryName()
	 */
	@Override
	public boolean hasCustomName() {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#getInventoryStackLimit()
	 */
	@Override
	public int getInventoryStackLimit()
	{
		return 64;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#markDirty()
	 */
	@Override
	public void markDirty() {
		for (int i = 0; i < getSizeInventory(); ++i)
		{
			if (getStackInSlot(i) != null && getStackInSlot(i).stackSize == 0) {
				inventory[i] = null;
			}
		}

		invItem.setStackDisplayName(this.name);
		// This line here does the work:		
		writeToNBT(invItem.getTagCompound());
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isUseableByPlayer(net.minecraft.entity.player.EntityPlayer)
	 */
	@Override
	public boolean isUseableByPlayer(EntityPlayer p_70300_1_) {
		return true;
	}

	/* (non-Javadoc)
	 * @see net.minecraft.inventory.IInventory#isItemValidForSlot(int, net.minecraft.item.ItemStack)
	 * This method doesn't seem to do what it claims to do, as
	 * items can still be left-clicked and placed in the inventory
	 * even when this returns false
	 */
	@Override
	public boolean isItemValidForSlot(int slot, ItemStack itemstack)
	{
		// Don't want to be able to store the inventory item within itself
		// Bad things will happen, like losing your inventory
		// Actually, this needs a custom Slot to work
		return !(itemstack.getItem() instanceof ItemFolder);
	}

	/**
	 * A custom method to read our inventory from an ItemStack's NBT compound
	 */
	public void readFromNBT(NBTTagCompound compound)
	{
		// Gets the custom taglist we wrote to this compound, if any
		NBTTagList items = compound.getTagList("ItemInventory", Constants.NBT.TAG_COMPOUND);

		for (int i = 0; i < items.tagCount(); ++i)
		{
			NBTTagCompound item = (NBTTagCompound) items.getCompoundTagAt(i);
			int slot = item.getInteger("Slot");

			// Just double-checking that the saved slot index is within our inventory array bounds
			if (slot >= 0 && slot < getSizeInventory()) {
				inventory[slot] = ItemStack.loadItemStackFromNBT(item);
			}
		}
	}

	/**
	 * A custom method to write our inventory to an ItemStack's NBT compound
	 */
	public void writeToNBT(NBTTagCompound tagcompound)
	{
		// Create a new NBT Tag List to store itemstacks as NBT Tags
		NBTTagList items = new NBTTagList();

		for (int i = 0; i < getSizeInventory(); ++i)
		{
			// Only write stacks that contain items
			if (getStackInSlot(i) != null)
			{
				// Make a new NBT Tag Compound to write the itemstack and slot index to
				NBTTagCompound item = new NBTTagCompound();
				item.setInteger("Slot", i);
				// Writes the itemstack in slot(i) to the Tag Compound we just made
				getStackInSlot(i).writeToNBT(item);

				// add the tag compound to our tag list
				items.appendTag(item);
			}
		}
		// Add the TagList to the ItemStack's Tag Compound with the name "ItemInventory"
		tagcompound.setTag("ItemInventory", items);
	}

	public void setInventoryName(String string) {
		OpenPrinter.logger.info(string);
		this.name = string;
		markDirty();
		//this.stack.setStackDisplayName(this.name);
	}

	@Override
	public IChatComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void openInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeInventory(EntityPlayer player) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getField(int id) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void setField(int id, int value) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public int getFieldCount() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public void clear() {
		// TODO Auto-generated method stub
		
	}
}
