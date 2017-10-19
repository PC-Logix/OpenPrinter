package pcl.openprinter;

/**
 * @author Caitlyn
 *
 */
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import pcl.openprinter.gui.GUIHandler;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.network.PacketHandler;
import pcl.openprinter.BuildInfo;
import pcl.openprinter.client.CreativeTab;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;


@Mod(modid=OpenPrinter.MODID, name="OpenPrinter", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "required-after:OpenComputers@[1.4.0,)")

public class OpenPrinter {

	public static final String MODID = "openprinter";
	@Instance(value = MODID)
	public static OpenPrinter instance;


	@SidedProxy(clientSide="pcl.openprinter.ClientProxy", serverSide="pcl.openprinter.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;

	private static boolean debug = true;
	public static final Logger  logger  = LogManager.getFormatterLogger(MODID);

	public static CreativeTabs CreativeTab = new CreativeTab("OpenPrinter");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {

		PacketHandler.init();
		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		ContentRegistry.registerAll(event);
		proxy.registerItemRenderers();
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		ContentRegistry.init();
		proxy.registerRenderers();
		FMLCommonHandler.instance().bus().register(this);
		NetworkRegistry.INSTANCE.registerGuiHandler(OpenPrinter.instance, new GUIHandler());
	}
	
	@SubscribeEvent
	public void handleCrafting (PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting.getItem() instanceof PrinterPaperRoll) {
			for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
				ItemStack item = event.craftMatrix.getStackInSlot(i);
				if (item != null)
					event.craftMatrix.setInventorySlotContents(i, new ItemStack(item.getItem(), 1, item.getItemDamage()));
			}
		}
	}
}
