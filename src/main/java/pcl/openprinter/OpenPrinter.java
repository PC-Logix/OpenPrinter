package pcl.openprinter;

/**
 * @author Caitlyn
 *
 */
import java.net.URL;
import java.util.logging.Logger;

import pcl.openprinter.blocks.Printer;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.gui.PrinterGUIHandler;
import pcl.openprinter.items.ItemPrinterBlock;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.BuildInfo;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.common.config.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
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
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import li.cil.oc.api.Blocks;
import li.cil.oc.api.CreativeTab;
import static li.cil.oc.api.Items.*;


@Mod(modid=OpenPrinter.MODID, name="OpenPrinter", version=BuildInfo.versionNumber + "." + BuildInfo.buildNumber, dependencies = "after:OpenComputers")

public class OpenPrinter {

	public static final String MODID = "openprinter";
	@Instance(value = MODID)
	public static OpenPrinter instance;
	public static Block printerBlock;
	public static Item  printedPage;
	public static Item  printerPaper;
	public static Item  printerPaperRoll;
	public static Item  printerInkColor;
	public static Item  printerInkBlack;
	public static ItemBlock  printeritemBlock;

	@SidedProxy(clientSide="pcl.openprinter.ClientProxy", serverSide="pcl.openprinter.CommonProxy")
	public static CommonProxy proxy;
	public static Config cfg = null;
	public static boolean render3D = true;

	private static boolean debug = true;
	public static org.apache.logging.log4j.Logger logger;

	@EventHandler
	public void preInit(FMLPreInitializationEvent event) {


		cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
		render3D = cfg.render3D;

		if((event.getSourceFile().getName().endsWith(".jar") || debug) && event.getSide().isClient() && cfg.enableMUD){
			try {
				Class.forName("pcl.openprinter.mud.ModUpdateDetector").getDeclaredMethod("registerMod", ModContainer.class, URL.class, URL.class).invoke(null,
						FMLCommonHandler.instance().findContainerFor(this),
						new URL("http://PC-Logix.com/OpenPrinter/get_latest_build_1.7.php"),
						new URL("http://PC-Logix.com/OpenPrinter/changelog1.7.txt")
						);
			} catch (Throwable e) {
				e.printStackTrace();
			}
		}
		logger = event.getModLog();



		GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");

		//Register Blocks)
		printerBlock = new Printer();
		GameRegistry.registerBlock(printerBlock, ItemPrinterBlock.class, "openprinter.printer");


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
		ItemStack nuggetIron   = IronNugget;
		ItemStack redstone     = new ItemStack(Items.redstone);
		ItemStack microchip    = MicrochipTier1;
		ItemStack pcb		   = PrintedCircuitBoard;
		ItemStack blackInk	   = new ItemStack(Items.dye, 1, 0);
		ItemStack redInk	   = new ItemStack(Items.dye, 1, 1);
		ItemStack greenInk	   = new ItemStack(Items.dye, 1, 2);
		ItemStack blueInk	   = new ItemStack(Items.dye, 1, 4);
		ItemStack paper        = new ItemStack(Items.paper);
		ItemStack lprinterPaper	= new ItemStack(printerPaper,64);
		ItemStack stackPaper	= new ItemStack(Items.paper,64);

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerBlock, 1), 
				"IRI",
				"MPM",
				"IRI",
				'I', nuggetIron, 'R', redstone, 'M', microchip, 'P', pcb));

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerInkBlack, 1), 
				"BBB",
				" I ",
				'B', blackInk, 'I', nuggetIron));

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerInkColor, 1), 
				"RGB",
				" I ",
				'R', redInk, 'G', greenInk, 'B', blueInk, 'I', nuggetIron));


		GameRegistry.addRecipe( new ItemStack(printerPaperRoll, 1), 
				"PP",
				"PP",
				'P', lprinterPaper);

		GameRegistry.addRecipe( new ItemStack(printerPaperRoll, 1), 
				"PP",
				"PP",
				'P', stackPaper);

		GameRegistry.addRecipe( new ItemStack(printerInkColor, 1),
				"RGB",
				" Z ",
				'R', redInk, 'G', greenInk, 'B', blueInk, 'Z', new ItemStack(printerInkColor, 1, OreDictionary.WILDCARD_VALUE));

		GameRegistry.addRecipe( new ItemStack(printerInkBlack, 1), 
				"BBB",
				" Z ",
				'B', blackInk, 'Z', new ItemStack(printerInkBlack, 1, OreDictionary.WILDCARD_VALUE));



		proxy.registerRenderers();

        NetworkRegistry.INSTANCE.registerGuiHandler(OpenPrinter.instance, new PrinterGUIHandler());
	}
}
