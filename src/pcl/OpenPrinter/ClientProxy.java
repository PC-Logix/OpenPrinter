package pcl.OpenPrinter;


import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.OpenPrinter.CommonProxy;
import pcl.OpenPrinter.TileEntity.PrinterGUIHandler;
import pcl.OpenPrinter.TileEntity.PrinterTE;
import pcl.OpenPrinter.TileEntityRender.PrinterRenderer;
import pcl.OpenPrinter.Blocks.Printer;

public class ClientProxy implements CommonProxy {
	@Override
	public void registerRenderers()
	{
		if (OpenPrinter.render3D) {
			RenderingRegistry.registerBlockHandler(Printer.block.getRenderType(), new PrinterRenderer());
		}
	}
}