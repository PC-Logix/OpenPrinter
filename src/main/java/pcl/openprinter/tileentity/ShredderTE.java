/**
 * 
 */
package pcl.openprinter.tileentity;

import java.util.Arrays;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.translation.I18n;

import pcl.openprinter.ContentRegistry;
import pcl.openprinter.items.PrintedPage;

/**
 * @author Caitlyn
 *
 */
public class ShredderTE extends TileEntity implements ITickable, IInventory, ISidedInventory {
	private ItemStack[] shredderItemStacks = new ItemStack[20];

	private int processingTime = 0;

	private static final int[] slots_top = new int[] {0};
	private static final int[] slots_bottom = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};
	private static final int[] slots_sides = new int[] {1,2,3,4,5,6,7,8,9,10,11,12,13,14,15,16,17,18};

	@Override
	public void readFromNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.readFromNBT(par1NBTTagCompound);
		NBTTagList var2 = par1NBTTagCompound.getTagList("Items",par1NBTTagCompound.getId());
		this.shredderItemStacks = new ItemStack[this.getSizeInventory()];
		for (int var3 = 0; var3 < var2.tagCount(); ++var3)
		{
			NBTTagCompound var4 = (NBTTagCompound)var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < this.shredderItemStacks.length)
			{
				this.shredderItemStacks[var5] = new ItemStack(var4);
			}
		}
	}

	@Override
	public NBTTagCompound writeToNBT(NBTTagCompound par1NBTTagCompound)
	{
		super.writeToNBT(par1NBTTagCompound);
		NBTTagList var2 = new NBTTagList();
		for (int var3 = 0; var3 < this.shredderItemStacks.length; ++var3)
		{

				NBTTagCompound var4 = new NBTTagCompound();
				var4.setByte("Slot", (byte)var3);
				this.shredderItemStacks[var3].writeToNBT(var4);
				var2.appendTag(var4);
		}
		par1NBTTagCompound.setTag("Items", var2);
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



	public ShredderTE() {
		Arrays.fill(shredderItemStacks, ItemStack.EMPTY);
	}

	@Override
	public int getSizeInventory() {
		return this.shredderItemStacks.length;
	}

	@Override
	public boolean isEmpty() {
		for(ItemStack s : shredderItemStacks)
		{
			if (!s.isEmpty()) return false;
		}
		return true;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.shredderItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int slot, int amt) {
		ItemStack stack = getStackInSlot(slot);
		if (!stack.isEmpty()) {
			if (stack.getCount() <= amt) {
				setInventorySlotContents(slot, ItemStack.EMPTY);
			} else {
				stack = stack.splitStack(amt);
				if (stack.getCount() == 0) {
					setInventorySlotContents(slot, ItemStack.EMPTY);
				}
			}
		}
		return stack;
	}

	public void incStackSize(int i, int amt) {

		if(shredderItemStacks[i].isEmpty())
			return;
		else if(shredderItemStacks[i].getCount() + amt > shredderItemStacks[i].getMaxStackSize())
			shredderItemStacks[i].setCount(shredderItemStacks[i].getMaxStackSize());
		else
			shredderItemStacks[i].setCount(shredderItemStacks[i].getCount() + amt);
	}

	@Override
	public ItemStack removeStackFromSlot(int i) {
		if (!getStackInSlot(i).isEmpty())
		{
			ItemStack var2 = getStackInSlot(i);
			setInventorySlotContents(i,ItemStack.EMPTY);
			return var2;
		}
		else
		{
			return ItemStack.EMPTY;
		}
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		this.shredderItemStacks[i] = itemstack;
		if (!itemstack.isEmpty() && itemstack.getCount() > this.getInventoryStackLimit())
		{
			itemstack.setCount(this.getInventoryStackLimit());
		}
	}


	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUsableByPlayer(EntityPlayer entityplayer) {
		return getWorld().getTileEntity(pos) == this &&
				entityplayer.getDistanceSq(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5) < 64;
	}


	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		if (i == 0 && itemstack.getItem() instanceof PrintedPage || itemstack.getItem().equals(Items.BOOK)) {
			return true;
		}
		return false;
	}

	@Override
	public String getName() {
		return "shredder";
	}

	@Override
	public void update() {
		boolean flag = this.processingTime > 0;

		if (!getStackInSlot(0).isEmpty()) {
			++this.processingTime;
			if (this.processingTime > 10) {
				for (int x = 1; x <= 9; x++) { //Loop the 18 output slots checking for a empty on
					if(!getStackInSlot(x).isEmpty() && getStackInSlot(x).getItem() instanceof pcl.openprinter.items.ItemPaperShreds && getStackInSlot(x).getCount() < 64) {
						if (getStackInSlot(0).getItem().equals(Items.BOOK) || getStackInSlot(0).getItem().equals(Items.WRITABLE_BOOK) || getStackInSlot(0).getItem().equals(Items.WRITTEN_BOOK)) {
							if (getStackInSlot(x).getCount() + 3 > 64 && x < 18) {
								for (int x2 = 1; x2 <= x - 9; x2++) {
									if(getStackInSlot(x2 + 1).isEmpty()) {
										this.shredderItemStacks[x + 1] = new ItemStack(ContentRegistry.shreddedPaper);
										if (64 - getStackInSlot(x).getCount() == 1) {
											incStackSize(x2 + 1, 64 - getStackInSlot(x).getCount());
										}
									} else {
										incStackSize(x2 + 1, 64 - getStackInSlot(x).getCount());
									}
								}
							}
							incStackSize(x, 3);
						} else {
							incStackSize(x, 1);
						}
						decrStackSize(0, 1);
						break;
					} else if (getStackInSlot(x).isEmpty()) {
						this.shredderItemStacks[x] = new ItemStack(ContentRegistry.shreddedPaper);
						if (getStackInSlot(0).getItem().equals(Items.BOOK) || getStackInSlot(0).getItem().equals(Items.WRITABLE_BOOK) || getStackInSlot(0).getItem().equals(Items.WRITTEN_BOOK)) {
							incStackSize(x, 2);
						}
						decrStackSize(0, 1);
						break;
					}
				}
				this.processingTime = 0;
			}
		}
	}

	@Override
	public boolean hasCustomName() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public ITextComponent getDisplayName() {
		return new TextComponentString(I18n.translateToLocal("gui.string.shredder"));
	}

	@Override
	public int[] getSlotsForFace(EnumFacing side) {
		return side == EnumFacing.DOWN ? slots_bottom : (side == EnumFacing.UP ? slots_top : slots_sides);
	}

	@Override
	public boolean canInsertItem(int slot, ItemStack stack, EnumFacing face) {
		return this.isItemValidForSlot(slot, stack);
	}

	@Override
	public boolean canExtractItem(int slot, ItemStack stack, EnumFacing face) {
		return true;
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
