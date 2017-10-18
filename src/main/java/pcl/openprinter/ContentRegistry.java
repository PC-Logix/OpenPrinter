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
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;

@EventBusSubscriber
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

	public static void registerAll(FMLPreInitializationEvent event) {
		registerBlocks(event, new BlockPrinter());
	}
	
	public static void registerBlocks(FMLPreInitializationEvent event, Block...blocks) {
		for (Block block : blocks) {
			final ItemBlock itemblock = new ItemBlock(block);
			if (event.getSide() == Side.CLIENT) {
				GameRegistry.register(block);
				GameRegistry.register(itemblock, block.getRegistryName());
				ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(block),0,new ModelResourceLocation(block.getRegistryName(), "inventory"));
				
			}
		}
	}
	
	public static void registerItems(FMLPreInitializationEvent event, Item...items) {
		for (Item item : items) {
			if (event.getSide() == Side.CLIENT) {
				GameRegistry.register(item);
				ModelLoader.setCustomModelResourceLocation(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
			}
		}
	}
	
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

	public static Item init(Item item, String name)
	{
		return item.setUnlocalizedName(name).setRegistryName("openprinter:" + name);
	}

	private static void registerItems() {
		shreddedPaper = new ItemPaperShreds();
		GameRegistry.register(init(shreddedPaper, "paper_shreds"));

		printerPaperRoll = new PrinterPaperRoll();
		GameRegistry.register(init(printerPaperRoll, "printer_paper_roll"));

		printerInkColor = new PrinterInkColor();
		GameRegistry.register(init(printerInkColor, "printer_ink_color"));

		printerInkBlack = new PrinterInkBlack();
		GameRegistry.register(init(printerInkBlack, "printer_ink_black"));

		printedPage = new PrintedPage();
		GameRegistry.register(init(printedPage, "printed_page"));
		
		folder = new ItemFolder();
		GameRegistry.register(init(folder, "folder"));
		
	}

	public static Block init(Block block, String name)
	{
		return block.setUnlocalizedName(name).setRegistryName("openprinter:" + name);
	}

	private static void registerBlocks() {
		//Register Blocks
		printerBlock = new BlockPrinter();
		GameRegistry.register(init(printerBlock, "printer"));

		shredderBlock = new BlockShredder();
		GameRegistry.register(init(shredderBlock, "shredder"));
		
		fileCabinetBlock = new BlockFileCabinet();
		GameRegistry.register(init(fileCabinetBlock, "filecabinet"));

		GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");
		GameRegistry.registerTileEntity(ShredderTE.class, "ShredderTE");
		GameRegistry.registerTileEntity(FileCabinetTE.class, "FileCabinetTE");
	}
	
	private static void registerRecipes() {
		ItemStack redstone		= new ItemStack(Items.REDSTONE);
		ItemStack shears		= new ItemStack(Items.SHEARS);
		ItemStack microchip		= li.cil.oc.api.Items.get("chip1").createItemStack(1);
		ItemStack pcb			= li.cil.oc.api.Items.get("printedcircuitboard").createItemStack(1);
		String blackInk			= "dyeBlack";
		String redInk			= "dyeRed";
		String greenInk			= "dyeGreen";
		String blueInk			= "dyeBlue";
		ItemStack paper         = new ItemStack(Items.PAPER);

		GameRegistry.addShapelessRecipe(paper, new Object[] { shreddedPaper, new ItemStack(Items.WATER_BUCKET) });

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
