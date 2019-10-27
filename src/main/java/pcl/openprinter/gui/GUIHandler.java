package pcl.openprinter.gui;

/**
 * @author Caitlyn
 */
import pcl.openprinter.inventory.*;
import pcl.openprinter.tileentity.FileCabinetTE;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentity.ShredderTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.network.IGuiHandler;

public class GUIHandler implements IGuiHandler {
	@Override
	public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity;
		switch(id) {
			case 0:
				//Printer Block GUI
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				if (tileEntity instanceof PrinterTE)
					return new PrinterContainer(player.inventory, (PrinterTE) tileEntity);
				break;
			case 1:
				//Client only Printed Page view
				break;
			case 2:
				//Shredder Block GUI
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				if (tileEntity instanceof ShredderTE)
					return new ShredderContainer(player.inventory, (ShredderTE) tileEntity);
				break;
			case 3:
				//Folder Container GUI
				// Use the player's held item to create the inventory
				return new FolderContainer(player, player.inventory);
			case 4:
				//Client only Folder View
				break;
			case 5:
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				if (tileEntity instanceof FileCabinetTE)
					return new FileCabinetContainer(player.inventory, (FileCabinetTE) tileEntity);
				break;
			case 6:
				// briefcase container for item
				return new BriefcaseContainer(player, player.inventory, null);
			case 7:
				// briefcase container for tileEntity in the world
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				return new BriefcaseContainer(player, player.inventory, tileEntity);
		}

		return null;
	}

	@Override
	public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
		TileEntity tileEntity;

		switch(id){
			case 0:
				//Printer Block GUI
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				if(tileEntity instanceof PrinterTE)
					return new PrinterGUI(player.inventory, (PrinterTE) tileEntity);
				break;
			case 1:
				//Printed Page GUI (Client only)
				return new PaperGUI(world, player);
			case 2:
				//Shredder Block GUI
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				if(tileEntity instanceof ShredderTE)
					return new ShredderGUI(player.inventory, (ShredderTE) tileEntity);
				break;
			case 3:
				return new GuiFolderInventory(new FolderContainer(player, player.inventory));
			case 4:
				//Folder view GUI (Client only)
				return new GuiFolderView(world, player);
			case 5:
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				return new FileCabinetGUI(player.inventory, (FileCabinetTE) tileEntity);
			case 6:
				// briefcase container for item
				return new BriefcaseInventory(new BriefcaseContainer(player, player.inventory, null));
			case 7:
				// briefcase container for tileEntity in the world
				tileEntity = world.getTileEntity(new BlockPos(x, y, z));
				return new BriefcaseInventory(new BriefcaseContainer(player, player.inventory, tileEntity));
		}

		return null;
	}
}
