package pcl.openprinter;

import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.client.model.obj.OBJLoader;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import pcl.openprinter.gui.GUIHandler;
import pcl.openprinter.manual.Manual;
import pcl.openprinter.network.PacketHandler;
import pcl.openprinter.client.CreativeTab;
import net.minecraftforge.common.config.Configuration;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.network.NetworkRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openprinter.util.ocLootDisk;


@Mod(
		modid=OpenPrinter.MODID,
		name="OpenPrinter",
		version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber,
		dependencies =
				"required-after:opencomputers;" +
				"after:rtfm"
)
@Mod.EventBusSubscriber
public class OpenPrinter {
	public static final String MODID = BuildInfo.modID;

	@Instance(value = MODID)
	public static OpenPrinter instance;

	public static Config cfg = null;

	public static boolean verbose = false;

	public static final Logger  logger  = LogManager.getFormatterLogger(MODID);

	public static CreativeTabs CreativeTab = new CreativeTab("OpenPrinter");
	
	private static ContentRegistry contentRegistry = new ContentRegistry();
	
	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		MinecraftForge.EVENT_BUS.register(contentRegistry);

		PacketHandler.init();
		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));

		Manual.preInit();

		contentRegistry.preInit();
	}

	@EventHandler
	public void postInit(FMLPostInitializationEvent event) {
		ocLootDisk.register();
	}

	@Mod.EventBusSubscriber
	public static class ObjectRegistryHandler {

		@SubscribeEvent
		@SideOnly(Side.CLIENT)
		public static void onRegisterModels(ModelRegistryEvent event) {
			OBJLoader.INSTANCE.addDomain(OpenPrinter.MODID);

			registerBlockModel(ContentRegistry.printerBlock, 0, "printer");
			registerBlockModel(ContentRegistry.shredderBlock, 0, "shredder");
			registerBlockModel(ContentRegistry.fileCabinetBlock, 0, "filecabinet");
			registerBlockModel(ContentRegistry.briefcaseBlock, 0, "briefcase");

			registerItemModel(ContentRegistry.printedPage, "printed_page");
			registerItemModel(ContentRegistry.printerInkBlack, "printer_ink_black");
			registerItemModel(ContentRegistry.printerInkColor, "printer_ink_color");
			registerItemModel(ContentRegistry.shreddedPaper, "shredded_paper");
			registerItemModel(ContentRegistry.folder, "folder");

			for(ItemStack item : ContentRegistry.modItems)
				ModelLoader.setCustomModelResourceLocation(item.getItem(), 0, new ModelResourceLocation(item.getItem().getRegistryName(), "inventory"));
		}

		@SideOnly(Side.CLIENT)
		private static void registerBlockModel(final Block block, int meta, final String blockName) {
			ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block), meta, new ModelResourceLocation(OpenPrinter.MODID + ":" + blockName.toLowerCase(), "inventory"));

			if(OpenPrinter.verbose)
				OpenPrinter.logger.info("Registering " + blockName.toLowerCase() + " Item Renderer");
		}

		@SideOnly(Side.CLIENT)
		private static void registerItemModel(final Item item, final String itemName) {
			ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(OpenPrinter.MODID + ":" + itemName.toLowerCase(), "inventory"));

			if(OpenPrinter.verbose)
				OpenPrinter.logger.info("Registering " + itemName.toLowerCase() + " Item Renderer");
		}
	}

	@EventHandler
	public void load(FMLInitializationEvent event) {
		NetworkRegistry.INSTANCE.registerGuiHandler(OpenPrinter.instance, new GUIHandler());
	}
}
