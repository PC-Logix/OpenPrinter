package pcl.openprinter;

import pcl.openprinter.blocks.BlockFileCabinet;
import pcl.openprinter.blocks.BlockPrinter;
import pcl.openprinter.blocks.BlockShredder;
import pcl.openprinter.items.ItemBlockFileCabinet;
import pcl.openprinter.items.ItemFolder;
import pcl.openprinter.items.ItemPaperShreds;
import pcl.openprinter.items.ItemPrinterBlock;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.items.PrinterPaperRollRecipe;
import pcl.openprinter.tileentity.FileCabinetTE;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentity.ShredderTE;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

public class ContentRegistry {
	
	public static CreativeTabs creativeTab;
	public static Block printerBlock;
	public static Block shredderBlock;
	public static Block fileCabinetBlock;
	public static Item  printedPage;
	public static Item  printerPaper;
	public static Item  printerPaperRoll;
	public static Item  printerInkColor;
	public static Item  printerInkBlack;
	public static Item  shreddedPaper;
	public static Item  folder;
	public static ItemBlock  printeritemBlock;

	private ContentRegistry() {
	}

	// Called on mod preInit()
	public static void preInit() {
        registerBlocks();
        registerItems();
	}

	//Called on mod init()
	public static void init() {
		registerRecipes();
	}

	private static void registerItems() {
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
		
		folder = new ItemFolder();
		GameRegistry.registerItem(folder, "openprinter.folder");
		
	}

	private static void registerBlocks() {
		//Register Blocks
		printerBlock = new BlockPrinter();
		GameRegistry.registerBlock(printerBlock, ItemPrinterBlock.class, "openprinter.printer");

		shredderBlock = new BlockShredder();
		GameRegistry.registerBlock(shredderBlock, "openprinter.shredder");
		
		fileCabinetBlock = new BlockFileCabinet();
		GameRegistry.registerBlock(fileCabinetBlock, ItemBlockFileCabinet.class, "openprinter.filecabinet");
		
		GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");
		GameRegistry.registerTileEntity(ShredderTE.class, "ShredderTE");
		GameRegistry.registerTileEntity(FileCabinetTE.class, "FileCabinetTE");
	}
	
	private static void registerRecipes() {
		ItemStack redstone		= new ItemStack(Items.redstone);
		ItemStack shears		= new ItemStack(Items.shears);
		ItemStack microchip		= li.cil.oc.api.Items.get("chip1").createItemStack(1);
		ItemStack pcb			= li.cil.oc.api.Items.get("printedCircuitBoard").createItemStack(1);
		String blackInk			= "dyeBlack";
		String redInk			= "dyeRed";
		String greenInk			= "dyeGreen";
		String blueInk			= "dyeBlue";
		ItemStack paper         = new ItemStack(Items.paper);

		GameRegistry.addShapelessRecipe(paper, new Object[] { shreddedPaper, new ItemStack(Items.water_bucket) });

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(printerBlock, 1), 
				"IRI",
				"MPM",
				"IRI",
				'I', "nuggetIron", 'R', redstone, 'M', microchip, 'P', pcb));

		GameRegistry.addRecipe(new ShapedOreRecipe( new ItemStack(shredderBlock, 1), 
				"IRI",
				"ISI",
				"IRI",
				'I', "nuggetIron", 'R', redstone, 'S', shears));
		
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
		
		GameRegistry.addRecipe(new ShapedOreRecipe(new ItemStack(folder, 1), 
				"P P",
				" P ",
				'P', paper));
		}
}
