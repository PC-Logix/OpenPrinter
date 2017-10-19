package pcl.openprinter;


import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ModelLoader;
import pcl.openprinter.CommonProxy;
import pcl.openprinter.tileentity.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;

public class ClientProxy extends CommonProxy {
	
	@Override
	public void registerItemRenderers() {
		//registerBlockItem(ContentRegistry.printerBlock, 0, "printer");
		//registerBlockItem(ContentRegistry.shredderBlock, 0, "shredder");
		//registerBlockItem(ContentRegistry.fileCabinetBlock, 0, "file_cabinet");
		//registerItem(ContentRegistry.printedPage, "printed_page");
		//registerItem(ContentRegistry.printerInkBlack, "printer_ink_black");
		//registerItem(ContentRegistry.printerInkColor, "printer_ink_color");
		//registerItem(ContentRegistry.printerPaperRoll, "printer_paper_roll");
		//registerItem(ContentRegistry.shreddedPaper, "shredded_paper");
		//registerItem(ContentRegistry.folder, "folder");
	}
	
	
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