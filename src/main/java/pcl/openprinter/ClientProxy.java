package pcl.openprinter;


import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.openprinter.CommonProxy;
import pcl.openprinter.tileentity.PrinterContainer;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentityrender.PrinterRenderer;
import pcl.openprinter.gui.PrinterGUIHandler;
import pcl.openprinter.itemrender.ItemPrinterRenderer;
import pcl.openprinter.blocks.Printer;

public class ClientProxy extends CommonProxy {
	
	public void registerRenderers()
	{
		if (OpenPrinter.render3D) {
			TileEntitySpecialRenderer render = new PrinterRenderer();
			ClientRegistry.bindTileEntitySpecialRenderer(pcl.openprinter.tileentity.PrinterTE.class, render);
			MinecraftForgeClient.registerItemRenderer(Item.getItemFromBlock(OpenPrinter.printerBlock), new ItemPrinterRenderer(render, new PrinterTE()));
		}
		NetworkRegistry.INSTANCE.registerGuiHandler(OpenPrinter.instance, new PrinterGUIHandler());
	}
	
    @Override
    public Object getClientGuiElement(int ID, EntityPlayer player, World world, int x, int y, int z)
    {
        TileEntity te = world.getTileEntity(x, y, z);
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
