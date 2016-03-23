/**
 * 
 */
package pcl.openprinter.gui;

/**
 * @author Caitlyn
 *
 */
import pcl.openprinter.items.FolderContainer;
import pcl.openprinter.items.FolderInventory;
import pcl.openprinter.tileentity.FileCabinetContainer;
import pcl.openprinter.tileentity.FileCabinetTE;
import pcl.openprinter.tileentity.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentity.ShredderContainer;
import pcl.openprinter.tileentity.ShredderTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) { //Printer Block GUI
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if(tileEntity instanceof PrinterTE){
				return new PrinterContainer(player.inventory, (PrinterTE) tileEntity);
			}
		} else if (id == 1) {
			//Client only Printed Page view
		} else if (id == 2) { //Shredder Block GUI
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if(tileEntity instanceof ShredderTE){
				return new ShredderContainer(player.inventory, (ShredderTE) tileEntity);
			}
		} else if (id == 3) { //Folder Container GUI
			// Use the player's held item to create the inventory
			return new FolderContainer(player, player.inventory, new FolderInventory(player.getHeldItem()));
		} else if (id == 4) {
			//Client only Folder View
		} else if (id == 5) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if(tileEntity instanceof FileCabinetTE){
				return new FileCabinetContainer(player.inventory, (FileCabinetTE) tileEntity);
			}
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		if (id == 0) { //Printer Block GUI
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if(tileEntity instanceof PrinterTE){
				return new PrinterGUI(player.inventory, (PrinterTE) tileEntity);
			}
		} else if (id == 1) { //Printed Page GUI (Client only)
			return new PaperGUI(world, player);
		} else if (id == 2) { //Shredder Block GUI
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			if(tileEntity instanceof ShredderTE){
				return new ShredderGUI(player.inventory, (ShredderTE) tileEntity);
			}
		} else if (id == 3) {
			return new GuiFolderInventory((FolderContainer) new FolderContainer(player, player.inventory, new FolderInventory(player.getHeldItem())));
		} else if (id == 4) { //Folder view GUI (Client only)
			return new GuiFolderView(world, player);
		} else if (id == 5) {
			TileEntity tileEntity = world.getTileEntity(x, y, z);
			return new FileCabinetGUI(player.inventory, (FileCabinetTE) tileEntity);
		}

		return null;
	}
}
