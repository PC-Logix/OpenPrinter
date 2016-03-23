/**
 * 
 */
package pcl.openprinter.tileentity;

import pcl.openprinter.ContentRegistry;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.items.PrintedPage;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.S35PacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;

/**
 * @author Caitlyn
 *
 */
public class FileCabinetTE extends TileEntity implements IInventory, ISidedInventory {
	private ItemStack[] fileCabinetItemStacks = new ItemStack[20];

	private int processingTime = 0;

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
	}

	@Override
	public void writeToNBT(NBTTagCompound par1NBTTagCompound)
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
	}

	@Override
	public net.minecraft.network.Packet getDescriptionPacket() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return new S35PacketUpdateTileEntity(xCoord, yCoord, zCoord, 1, tag);
	}

	@Override
	public void onDataPacket(NetworkManager net, S35PacketUpdateTileEntity packet) {
		readFromNBT(packet.func_148857_g());
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
	public ItemStack getStackInSlotOnClosing(int i) {
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
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		return worldObj.getTileEntity(xCoord, yCoord, zCoord) == this &&
				entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}


	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0) {
			if (itemstack.getItem() instanceof PrintedPage || itemstack.getItem().equals(Items.book)) {
				return true;
			}
		}
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int par1) {
		return par1 == 0 ? slots_bottom : (par1 == 1 ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		return this.isItemValidForSlot(i, itemstack);
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		return true;
	}

	@Override
	public String getInventoryName() {
		return "fileCabinet";
	}

//	public void updateEntity() {
//		boolean flag = this.processingTime > 0;
//
//		if (getStackInSlot(0) != null) {
//			++this.processingTime;
//			if (this.processingTime > 10) {
//				for (int x = 1; x <= 9; x++) { //Loop the 18 output slots checking for a empty on
//					if(getStackInSlot(x) != null && getStackInSlot(x).getItem() instanceof pcl.openprinter.items.ItemPaperShreds && getStackInSlot(x).stackSize < 64) {
//						if (getStackInSlot(0).getItem().equals(Items.book) || getStackInSlot(0).getItem().equals(Items.writable_book) || getStackInSlot(0).getItem().equals(Items.written_book)) {
//							if (getStackInSlot(x).stackSize + 3 > 64) {
//								if (x < 18) {
//									for (int x2 = 1; x2 <= x - 9; x2++) {
//										if(getStackInSlot(x2 + 1) == null) {
//											this.fileCabinetItemStacks[x + 1] = new ItemStack(ContentRegistry.shreddedPaper);
//											if (64 - getStackInSlot(x).stackSize == 1) {
//												incStackSize(x2 + 1, 64 - getStackInSlot(x).stackSize);					
//											}
//										} else {
//											incStackSize(x2 + 1, 64 - getStackInSlot(x).stackSize);
//										}
//									}
//								}
//							}
//							incStackSize(x, 3);
//						} else {
//							incStackSize(x, 1);
//						}
//						decrStackSize(0, 1);
//						break;
//					} else if (getStackInSlot(x) == null) {
//						this.fileCabinetItemStacks[x] = new ItemStack(ContentRegistry.shreddedPaper);
//						if (getStackInSlot(0).getItem().equals(Items.book) || getStackInSlot(0).getItem().equals(Items.writable_book) || getStackInSlot(0).getItem().equals(Items.written_book)) {
//							incStackSize(x, 2);
//						}
//						decrStackSize(0, 1);
//						break;
//					}
//				}
//				this.processingTime = 0;
//			}
//		}
//	}

	@Override
	public boolean hasCustomInventoryName() {
		return false;
	}

	@Override
	public void openInventory() {
	}

	@Override
	public void closeInventory() {
	}
}
