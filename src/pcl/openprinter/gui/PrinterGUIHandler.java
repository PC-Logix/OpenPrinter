/**
 * 
 */
package pcl.openprinter.gui;

/**
 * @author Caitlyn
 *
 */
import pcl.openprinter.tileentity.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class PrinterGUIHandler implements IGuiHandler {
        //returns an instance of the Container you made earlier
        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        	if (id == 0) {
                TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
                if(tileEntity instanceof PrinterTE){
                        return new PrinterContainer(player.inventory, (PrinterTE) tileEntity);
                }
        	}

                return null;
        }

        //returns an instance of the Gui you made earlier
        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        	if (id == 0) {
                TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
                if(tileEntity instanceof PrinterTE){
                        return new PrinterGUI(player.inventory, (PrinterTE) tileEntity);
                }
        	} else if (id == 1) {
        		return new PaperGUI(world, player);
        	}

                return null;
        }
}
