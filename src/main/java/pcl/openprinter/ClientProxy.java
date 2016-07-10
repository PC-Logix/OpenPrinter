package pcl.openprinter;


import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;
import pcl.openprinter.CommonProxy;
import pcl.openprinter.tileentity.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;

public class ClientProxy extends CommonProxy {
	
    @Override
    public Object getClientGuiElement(int id, EntityPlayer player, World world, int x, int y, int z) {
        TileEntity te = world.getTileEntity(new BlockPos(x, y, z));
        if (te != null && te instanceof PrinterTE)
        {
        	PrinterTE icte = (PrinterTE) te;
            return new PrinterContainer(player.inventory, icte);
        }
        else
        {
            return null;
        }
    }
	
}