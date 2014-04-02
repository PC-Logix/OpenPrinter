/**
 * 
 */
package pcl.OpenPrinter.TileEntity;

/**
 * @author Caitlyn
 *
 */
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import cpw.mods.fml.common.network.IGuiHandler;

public class PrinterGUIHandler implements IGuiHandler {
        //returns an instance of the Container you made earlier
        @Override
        public Object getServerGuiElement(int id, EntityPlayer player, World world,
                        int x, int y, int z) {
                TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
                if(tileEntity instanceof PrinterTE){
                        return new PrinterContainer(player.inventory, (PrinterTE) tileEntity);
                }
                return null;
        }

        //returns an instance of the Gui you made earlier
        @Override
        public Object getClientGuiElement(int id, EntityPlayer player, World world,
                        int x, int y, int z) {
                TileEntity tileEntity = world.getBlockTileEntity(x, y, z);
                if(tileEntity instanceof PrinterTE){
                        return new PrinterGUI(player.inventory, (PrinterTE) tileEntity);
                }
                return null;

        }
}
