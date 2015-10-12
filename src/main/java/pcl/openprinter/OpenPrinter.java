package pcl.openprinter;

/**
 * @author Caitlyn
 *
 */
import java.net.URL;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import cpw.mods.fml.common.eventhandler.SubscribeEvent;
import cpw.mods.fml.common.gameevent.PlayerEvent;
import pcl.openprinter.blocks.BlockPrinter;
import pcl.openprinter.blocks.BlockShredder;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentity.ShredderTE;
import pcl.openprinter.gui.GUIHandler;
import pcl.openprinter.items.ItemPaperShreds;
import pcl.openprinter.items.ItemPrinterBlock;
import pcl.openprinter.items.ItemShredderBlock;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.items.PrinterPaperRollRecipe;
import pcl.openprinter.BuildInfo;
import pcl.openprinter.client.CreativeTab;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.FMLCommonHandler;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.ModContainer;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;


@Mod(modid=OpenPrinter.MODID, name="OpenPrinter", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "required-after:OpenComputers@[1.4.0,)")

public class OpenPrinter {

	public static final String MODID = "openprinter";
	@Instance(value = MODID)
	public static OpenPrinter instance;
	public static Block printerBlock;
	public static Block shredderBlock;
	public static Item  printedPage;
	public static Item  printerPaper;
	public static Item  printerPaperRoll;
	public static Item  printerInkColor;
	public static Item  printerInkBlack;
	public static Item  shreddedPaper;
	public static ItemBlock  printeritemBlock;

	@SidedProxy(clientSide="pcl.openprinter.ClientProxy", serverSide="pcl.openprinter.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;
	public static boolean render3D = true;

	private static boolean debug = true;
	public static final Logger  logger  = LogManager.getFormatterLogger(MODID);

	public static CreativeTabs CreativeTab = new CreativeTab("OpenPrinter");

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {


		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		render3D = cfg.render3D;

		if ((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD) {
			logger.info("Registering mod with OpenUpdater");
			try {
				Class.forName("pcl.mud.OpenUpdater").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null, FMLCommonHandler.instance().findContainerFor(this),
						new URL("http://PC-Logix.com/OpenPrinter/get_latest_build.php?mcver=1.7.10"),
						new URL("http://PC-Logix.com/OpenPrinter/changelog.php?mcver=1.7.10"));
			} catch (Throwable e) {
				logger.info("OpenUpdater is not installed, not registering.");
			}
		}


		GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");
		GameRegistry.registerTileEntity(ShredderTE.class, "ShredderTE");

		//Register Blocks)
		printerBlock = new BlockPrinter();
		GameRegistry.registerBlock(printerBlock, ItemPrinterBlock.class, "openprinter.printer");

		shredderBlock = new BlockShredder();
		GameRegistry.registerBlock(shredderBlock, ItemShredderBlock.class, "openprinter.shredder");

		shreddedPaper = new ItemPaperShreds();
		GameRegistry.registerItem(shreddedPaper, "openprinter.paperShreds");

		printerPaperRoll = new PrinterPaperRoll();
		GameRegistry.registerItem(printerPaperRoll, "openprinter.printerPaperRoll");


		printerInkColor = new PrinterInkColor();
		GameRegistry.registerItem(printerInkColor, "openprinter.printerInkColor");


		printerInkBlack = new PrinterInkBlack();
		GameRegistry.registerItem(printerInkBlack, "openprinter.printerInkBlack");


		printedPage = new PrintedPage();
		GameRegistry.registerItem(printedPage, "openprinter.printedPage");

	}

	@EventHandler
	public void load(FMLInitializationEvent event)
	{
		ItemStack redstone      = new ItemStack(Items.redstone);
		ItemStack microchip    	= li.cil.oc.api.Items.get("chip1").createItemStack(1);
		ItemStack pcb		   	= li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
		String blackInk	    = "dyeBlack";
		String redInk	    = "dyeRed";
		String greenInk	    = "dyeGreen";
		String blueInk	    = "dyeBlue";
		ItemStack paper         = new ItemStack(Items.paper);

		GameRegistry.addShapelessRecipe(paper, new Object[] { shreddedPaper, new ItemStack(Items.water_bucket) });

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerBlock, 1), 
				"IRI",
				"MPM",
				"IRI",
				'I', "nuggetIron", 'R', redstone, 'M', microchip, 'P', pcb));

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerInkBlack, 1), 
				"BBB",
				" I ",
				'B', blackInk, 'I', "nuggetIron"));

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerInkColor, 1), 
				"RGB",
				" I ",
				'R', redInk, 'G', greenInk, 'B', blueInk, 'I', "nuggetIron"));

		GameRegistry.addRecipe(new PrinterPaperRollRecipe());

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(printerInkColor, 1),
				"RGB",
				" Z ",
				'R', redInk, 'G', greenInk, 'B', blueInk, 'Z', new ItemStack(printerInkColor, 1, OreDictionary.WILDCARD_VALUE)));

		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(printerInkBlack, 1), 
				"BBB",
				" Z ",
				'B', blackInk, 'Z', new ItemStack(printerInkBlack, 1, OreDictionary.WILDCARD_VALUE)));


		FMLCommonHandler.instance().bus().register(this);
		proxy.registerRenderers();

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
