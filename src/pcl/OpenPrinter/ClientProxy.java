package pcl.OpenPrinter;


import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraftforge.client.IItemRenderer;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.OpenPrinter.CommonProxy;
import pcl.OpenPrinter.TileEntity.PrinterGUIHandler;
import pcl.OpenPrinter.TileEntity.PrinterTE;
import pcl.OpenPrinter.TileEntityRender.PrinterRenderer;
import pcl.OpenPrinter.itemrender.ItemPrinterRenderer;
import pcl.OpenPrinter.Blocks.Printer;

public class ClientProxy implements CommonProxy {
	@Override
	public void registerRenderers()
	{
		if (OpenPrinter.render3D) {
			TileEntitySpecialRenderer render = new PrinterRenderer();
			ClientRegistry.bindTileEntitySpecialRenderer(pcl.OpenPrinter.TileEntity.PrinterTE.class, render);
			MinecraftForgeClient.registerItemRenderer(OpenPrinter.cfg.printerBlockID, new ItemPrinterRenderer(render, new PrinterTE()));
		}
	}
}