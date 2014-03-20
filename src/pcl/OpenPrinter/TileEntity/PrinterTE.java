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
public class PrinterTE extends TileEntity implements SimpleComponent {
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
 
public PrinterTE() {
	
}
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
}
