/**
 * 
 */
package pcl.openprinter.tileentity;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.text.ITextComponent;
import pcl.openprinter.items.PrintedPage;

/**
 * @author Caitlyn
 *
 */
public class FileCabinetTE extends TileEntity implements IInventory {
	public ItemStack[] fileCabinetItemStacks = new ItemStack[30];

	public String name = "";

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
	private static final int[] slots_sides = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items",par1NBTTagCompound.getId());
		this.fileCabinetItemStacks = new ItemStack[this.getSizeInventory()];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.fileCabinetItemStacks.length)
			{
				this.fileCabinetItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
			}
		}
		this.name = par1NBTTagCompound.getString("name");
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.fileCabinetItemStacks.length; ++var3)
		{
			if (this.fileCabinetItemStacks[var3] != null)
			{
				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.fileCabinetItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
			}
		}
		par1NBTTagCompound.setTag("Items", var2);
		
		par1NBTTagCompound.setString("name", this.name);
		return par1NBTTagCompound;
	}

	@Override
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return tag;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}


	public FileCabinetTE() { }

	@Override
	public int getSizeInventory() {
		return this.fileCabinetItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.fileCabinetItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (stack != null) {
			if (stack.stackSize <= amt) {
				setInventorySlotContents(slot, null);
			} else {
				stack = stack.splitStack(amt);
				if (stack.stackSize == 0) {
					setInventorySlotContents(slot, null);
				}
			}
		}
		return stack;
	}

	public void incStackSize(int i, int amt) {

		if(fileCabinetItemStacks[i] == null)
			return;
		else if(fileCabinetItemStacks[i].stackSize + amt > fileCabinetItemStacks[i].getMaxStackSize())
			fileCabinetItemStacks[i].stackSize = fileCabinetItemStacks[i].getMaxStackSize();
		else
			fileCabinetItemStacks[i].stackSize += amt;
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (getStackInSlot(i) != null)
		{
			ItemStack var2 = getStackInSlot(i);
			setInventorySlotContents(i,null);
			return var2;
		}
		else
		{
			return null;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.fileCabinetItemStacks[i] = itemstack;
		if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
		{
			itemstack.stackSize = this.getInventoryStackLimit();
		}
	}


	@Override
	public int getInventoryStackLimit() {
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(pos) == this &&
				entityplayer.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64;
	}


	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0 && (itemstack.getItem() instanceof PrintedPage || itemstack.getItem().equals(Items.BOOK))) {
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "fileCabinet";
	}

	@Override
	public boolean hasCustomName() {
		return false;
	}

	public boolean hasDisplayName() {
		return name.length() > 0;
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

	@Override
	public ITextComponent getDisplayName() {
		// TODO Auto-generated method stub
		return null;
	}
}
