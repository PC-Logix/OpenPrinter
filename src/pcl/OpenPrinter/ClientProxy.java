package pcl.OpenPrinter;


import cpw.mods.fml.client.registry.ClientRegistry;
import cpw.mods.fml.client.registry.RenderingRegistry;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.client.MinecraftForgeClient;
import pcl.OpenPrinter.CommonProxy;
import pcl.OpenPrinter.TileEntity.PrinterTE;
import pcl.OpenPrinter.TileEntityRender.PrinterRenderer;
import pcl.OpenPrinter.Blocks.BlockPrinter;

public class ClientProxy implements CommonProxy {
	@Override
	public void registerRenderers()
	{
		RenderingRegistry.registerBlockHandler(BlockPrinter.block.getRenderType(), new PrinterRenderer());
	}
	
}