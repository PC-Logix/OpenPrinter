package pcl.openprinter;

/**
 * @author Caitlyn
 *
 */
import java.net.URL;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraftforge.client.model.ModelLoader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent;
import pcl.openprinter.gui.GUIHandler;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.network.PacketHandler;
import pcl.openprinter.client.CreativeTab;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;


@Mod(
		modid=OpenPrinter.MODID,
		name="OpenPrinter",
		version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber,
		dependencies = "required-after:OpenComputers@[1.7.1,)"
)

public class OpenPrinter {
	public static final String MODID = BuildInfo.modID;

	@Instance(value = MODID)
	public static OpenPrinter instance;

	public static Config cfg = null;

	private static boolean debug = true;
	public static final Logger  logger  = LogManager.getFormatterLogger(MODID);

	public static CreativeTabs CreativeTab = new CreativeTab("OpenPrinter");
	
	private static ContentRegistry contentRegistry = new ContentRegistry();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.register(contentRegistry);
		
		FMLCommonHandler.instance().bus().register(this);
		PacketHandler.init();
		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));

		if ((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD) {
			logger.info("Registering mod with OpenUpdater");
			try {
				Class.forName("pcl.mud.OpenUpdater").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null, FMLCommonHandler.instance().findContainerFor(this),
						new URL("http://PC-Logix.com/OpenPrinter/get_latest_build.php?mcver=1.12.2"),
						new URL("http://PC-Logix.com/OpenPrinter/changelog.php?mcver=1.12.2"));
			} catch (Throwable e) {
				logger.info("OpenUpdater is not installed, not registering.");
			}
		}
		contentRegistry.preInit();
	}
	
	@SubscribeEvent
	@SideOnly(Side.CLIENT)
	public void onRegisterModels(ModelRegistryEvent event) {
		registerBlockModel(ContentRegistry.printerBlock, 0, "printer");
		registerBlockModel(ContentRegistry.shredderBlock, 0, "shredder");
		registerBlockModel(ContentRegistry.fileCabinetBlock, 0, "filecabinet");
		registerItemModel(ContentRegistry.printedPage, "printed_page");
		registerItemModel(ContentRegistry.printerInkBlack, "printer_ink_black");
		registerItemModel(ContentRegistry.printerInkColor, "printer_ink_color");
		registerItemModel(ContentRegistry.printerPaperRoll, "printer_paper_roll");
		registerItemModel(ContentRegistry.shreddedPaper, "shredded_paper");
		registerItemModel(ContentRegistry.folder, "folder");
	}

	@SideOnly(Side.CLIENT)
	private static void registerBlockModel(final Block block, int meta, final String blockName) {
		ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(OpenPrinter.MODID + ":" + blockName.toLowerCase(), "inventory"));
		OpenPrinter.logger.info("Registering " + blockName.toLowerCase() + " Item Renderer");
	}

	@SideOnly(Side.CLIENT)
	private static void registerItemModel(final Item item, final String itemName) {
		ModelLoader.setCustomModelResourceLocation(item,  0, new ModelResourceLocation(OpenPrinter.MODID + ":" + itemName.toLowerCase(), "inventory"));
		OpenPrinter.logger.info("Registering " + itemName.toLowerCase() + " Item Renderer");
	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		NetworkRegistry.INSTANCE.registerGuiHandler(OpenPrinter.instance, new GUIHandler());
	}
	
	@SubscribeEvent
	public void handleCrafting (PlayerEvent.ItemCraftedEvent event) {
		if (event.crafting.getItem() instanceof PrinterPaperRoll) {
			for (int i = 0; i < event.craftMatrix.getSizeInventory(); i++) {
				ItemStack item = event.craftMatrix.getStackInSlot(i);
				if (!item.isEmpty())
					event.craftMatrix.setInventorySlotContents(i, new ItemStack(item.getItem(), 1, item.getItemDamage()));
			}
		}
	}
}
