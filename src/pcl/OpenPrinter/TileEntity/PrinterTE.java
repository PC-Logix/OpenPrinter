/**
 * 
 */
package pcl.openprinter.tileentity;

import li.cil.oc.api.Network;
import li.cil.oc.api.network.Arguments;
import li.cil.oc.api.network.Callback;
import li.cil.oc.api.network.Context;
import li.cil.oc.api.network.ManagedEnvironment;
import li.cil.oc.api.network.Message;
import li.cil.oc.api.network.Node;
import li.cil.oc.api.network.SimpleComponent;
import li.cil.oc.api.network.Visibility;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class PrinterTE extends TileEntity implements SimpleComponent, IInventory, ISidedInventory {
	private ItemStack[] printerItemStacks = new ItemStack[20];

	   @Override
       public void readFromNBT(NBTTagCompound par1NBTTagCompound)
       {
               super.readFromNBT(par1NBTTagCompound);
               NBTTagList var2 = par1NBTTagCompound.getTagList("Items");
               this.printerItemStacks = new ItemStack[this.getSizeInventory()];
               for (int var3 = 0; var3 < var2.tagCount(); ++var3)
               {
                       NBTTagCompound var4 = (NBTTagCompound)var2.tagAt(var3);
                       byte var5 = var4.getByte("Slot");
                       if (var5 >= 0 && var5 < this.printerItemStacks.length)
                       {
                               this.printerItemStacks[var5] = ItemStack.loadItemStackFromNBT(var4);
                       }
               }
       }

	   @Override
       public void writeToNBT(NBTTagCompound par1NBTTagCompound)
       {
               super.writeToNBT(par1NBTTagCompound);
               NBTTagList var2 = new NBTTagList();
               for (int var3 = 0; var3 < this.printerItemStacks.length; ++var3)
               {
                       if (this.printerItemStacks[var3] != null)
                       {
                               NBTTagCompound var4 = new NBTTagCompound();
                               var4.setByte("Slot", (byte)var3);
                               this.printerItemStacks[var3].writeToNBT(var4);
                               var2.appendTag(var4);
                       }
               }
               par1NBTTagCompound.setTag("Items", var2);
       }
 
	   public PrinterTE() { }
	@Override
	public String getComponentName() {
		// TODO Auto-generated method stub
		return "openprinter";
	}
	
	@Callback
	public Object[] greet(Context context, Arguments args) {
		return new Object[] { "Lasciate ogne speranza, voi ch'intrate" };
	}
	
	@Callback
	public Object[] print(Context context, Arguments args) {
		return new Object[] { "I'm sorry, Dave, I'm afraid I can't do that." };
	}
	
	@Callback
	public Object[] scan(Context context, Arguments args) {
		return new Object[] { "I'm sorry, Dave, I'm afraid I can't do that." };
	}
	
	
	//Real Printer methods follow:
	@Callback
	public Object[] write(Context context, Arguments args) {
		printerItemStacks[3] = new ItemStack(printerItemStacks[2].getItem());
		printerItemStacks[3].setTagCompound(new NBTTagCompound());
		printerItemStacks[3].stackTagCompound.setString("line1", "TEXT!");
		
		return new Object[] { "I'm sorry, Dave, I'm afraid I can't do that." };
	}
	@Callback
	public Object[] writeln(Context context, Arguments args) {
		return new Object[] { "I'm sorry, Dave, I'm afraid I can't do that." };
	}
	@Callback
	public Object[] setTitle(Context context, Arguments args) {
		return new Object[] { "I'm sorry, Dave, I'm afraid I can't do that." };
	}

	@Override
	public int getSizeInventory() {
		return this.printerItemStacks.length;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		return this.printerItemStacks[i];
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
        if (this.printerItemStacks[i] != null)
        {
                ItemStack var2 = this.printerItemStacks[i];
                this.printerItemStacks[i] = null;
                return var2;
        }
        else
        {
                return null;
        }
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
        this.printerItemStacks[i] = itemstack;
        if (itemstack != null && itemstack.stackSize > this.getInventoryStackLimit())
        {
        	itemstack.stackSize = this.getInventoryStackLimit();
        }
	}

	@Override
	public String getInvName() {
		// TODO Auto-generated method stub
		return "openprinter";
	}

	@Override
	public boolean isInvNameLocalized() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int getInventoryStackLimit() {
		// TODO Auto-generated method stub
		return 64;
	}

	@Override
	public boolean isUseableByPlayer(EntityPlayer entityplayer) {
		// TODO Auto-generated method stub
        return worldObj.getBlockTileEntity(xCoord, yCoord, zCoord) == this &&
        entityplayer.getDistanceSq(xCoord + 0.5, yCoord + 0.5, zCoord + 0.5) < 64;
	}

	@Override
	public void openChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void closeChest() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public boolean isItemValidForSlot(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int[] getAccessibleSlotsFromSide(int var1) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public boolean canInsertItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean canExtractItem(int i, ItemStack itemstack, int j) {
		// TODO Auto-generated method stub
		return false;
	}
}
