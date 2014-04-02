/**
 * 
 */
package pcl.OpenPrinter.TileEntity;

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
import net.minecraft.inventory.ISidedInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.INetworkManager;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.Packet132TileEntityData;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class PrinterTE extends TileEntity implements SimpleComponent, ISidedInventory {
	   @Override
	   public void writeToNBT(NBTTagCompound par1)
	   {
	      super.writeToNBT(par1);
	   }

	   @Override
	   public void readFromNBT(NBTTagCompound par1)
	   {
	      super.readFromNBT(par1);
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
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public ItemStack getStackInSlot(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack decrStackSize(int i, int j) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public ItemStack getStackInSlotOnClosing(int i) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setInventorySlotContents(int i, ItemStack itemstack) {
		// TODO Auto-generated method stub
		
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
		return 0;
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
