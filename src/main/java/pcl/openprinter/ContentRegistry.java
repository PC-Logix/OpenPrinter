package pcl.openprinter;

import net.minecraft.init.Blocks;
import pcl.openprinter.blocks.BlockFileCabinet;
import pcl.openprinter.blocks.BlockPrinter;
import pcl.openprinter.blocks.BlockShredder;
import pcl.openprinter.items.ItemFolder;
import pcl.openprinter.items.ItemPaperShreds;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaperRoll;
import pcl.openprinter.items.PrinterPaperRollRecipe;
import pcl.openprinter.tileentity.FileCabinetTE;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentity.ShredderTE;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.IRecipe;

import net.minecraftforge.oredict.OreDictionary;
import net.minecraftforge.oredict.ShapedOreRecipe;
import net.minecraftforge.oredict.ShapelessOreRecipe;

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

	protected ContentRegistry() {
	}

	// Called on mod preInit()
	public void preInit() {
		printerBlock =init(new BlockPrinter(), "printer");
		shredderBlock = init(new BlockShredder(), "shredder");
		fileCabinetBlock = init(new BlockFileCabinet(), "filecabinet");
		GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");
		GameRegistry.registerTileEntity(ShredderTE.class, "ShredderTE");
		GameRegistry.registerTileEntity(FileCabinetTE.class, "FileCabinetTE");
	}

	public static Item init(Item item, String name)
	{
		return item.setUnlocalizedName(name).setRegistryName("openprinter:" + name);
	}
	
	@SubscribeEvent
	public void registerItems(RegistryEvent.Register<Item> register) {
		shreddedPaper = new ItemPaperShreds();
		register.getRegistry().register(init(shreddedPaper, "paper_shreds"));
		
		printerPaperRoll = new PrinterPaperRoll();
		register.getRegistry().register(init(printerPaperRoll, "printer_paper_roll"));
		
		printerInkColor = new PrinterInkColor();
		register.getRegistry().register(init(printerInkColor, "printer_ink_color"));
		
		printerInkBlack = new PrinterInkBlack();
		register.getRegistry().register(init(printerInkBlack, "printer_ink_black"));
		
		printedPage = new PrintedPage();
		register.getRegistry().register(init(printedPage, "printed_page"));
		
		folder = new ItemFolder();
		register.getRegistry().register(init(folder, "folder"));
		
		register.getRegistry().register(new ItemBlock(printerBlock).setCreativeTab(OpenPrinter.CreativeTab).setRegistryName(printerBlock.getRegistryName()));
		register.getRegistry().register(new ItemBlock(shredderBlock).setCreativeTab(OpenPrinter.CreativeTab).setRegistryName(shredderBlock.getRegistryName()));
		register.getRegistry().register(new ItemBlock(fileCabinetBlock).setCreativeTab(OpenPrinter.CreativeTab).setRegistryName(fileCabinetBlock.getRegistryName()));
	}
	
	@SubscribeEvent
	public void registerBlocks(RegistryEvent.Register<Block> register) {
		register.getRegistry().register(printerBlock);
		register.getRegistry().register(shredderBlock);
		register.getRegistry().register(fileCabinetBlock);
	}
	


	public static Block init(Block block, String name)
	{
		return block.setUnlocalizedName(name).setRegistryName("openprinter:" + name);
	}
	

	//todo: make json recipes
	@SubscribeEvent
	public void registerRecipes(RegistryEvent.Register<IRecipe> event) {
		ItemStack redstone		= new ItemStack(Items.REDSTONE);
		ItemStack shears		= new ItemStack(Items.SHEARS);
		ItemStack chest			= new ItemStack(Blocks.CHEST);
		ItemStack microchip		= li.cil.oc.api.Items.get("chip1").createItemStack(1);
		ItemStack pcb			= li.cil.oc.api.Items.get("printedcircuitboard").createItemStack(1);
		String blackInk			= "dyeBlack";
		String redInk			= "dyeRed";
		String greenInk			= "dyeGreen";
		String blueInk			= "dyeBlue";
		ItemStack paper         = new ItemStack(Items.PAPER);

		event.getRegistry().register(new ShapelessOreRecipe(paper.getItem().getRegistryName(), paper,  shreddedPaper, new ItemStack(Items.WATER_BUCKET)).setRegistryName(OpenPrinter.MODID,"paper") );

		event.getRegistry().register(new ShapedOreRecipe(printerBlock.getRegistryName(), new ItemStack(printerBlock, 1),
				"IRI",
				"MPM",
				"IRI",
				'I', "nuggetIron", 'R', redstone, 'M', microchip, 'P', pcb).setRegistryName(OpenPrinter.MODID,"printer"));

		event.getRegistry().register(new ShapedOreRecipe(shredderBlock.getRegistryName(), new ItemStack(shredderBlock, 1),
				"IRI",
				"ISI",
				"IRI",
				'I', "nuggetIron", 'R', redstone, 'S', shears).setRegistryName(OpenPrinter.MODID,"shredder"));

		event.getRegistry().register(new ShapedOreRecipe(fileCabinetBlock.getRegistryName(), new ItemStack(fileCabinetBlock, 1),
				"I I",
				"ICI",
				"I I",
				'I', "nuggetIron", 'C', chest).setRegistryName(OpenPrinter.MODID,"filecabinet"));
		
		event.getRegistry().register(new ShapedOreRecipe(printerInkBlack.getRegistryName(), new ItemStack(printerInkBlack, 1),
				"BBB",
				" I ",
				'B', blackInk, 'I', "nuggetIron").setRegistryName(OpenPrinter.MODID,"printer_ink_black_1"));

		event.getRegistry().register(new ShapedOreRecipe(printerInkColor.getRegistryName(), new ItemStack(printerInkColor, 1),
				"RGB",
				" I ",
				'R', redInk, 'G', greenInk, 'B', blueInk, 'I', "nuggetIron").setRegistryName(OpenPrinter.MODID,"printer_ink_color_1"));

		event.getRegistry().register(new PrinterPaperRollRecipe().setRegistryName(OpenPrinter.MODID, "printer_paper_roll"));

		event.getRegistry().register(new ShapedOreRecipe(printerInkColor.getRegistryName(), new ItemStack(printerInkColor, 1),
				"RGB",
				" Z ",
				'R', redInk, 'G', greenInk, 'B', blueInk, 'Z', new ItemStack(printerInkColor, 1, OreDictionary.WILDCARD_VALUE)).setRegistryName(OpenPrinter.MODID,"printer_ink_color_2"));

		event.getRegistry().register(new ShapedOreRecipe(printerInkBlack.getRegistryName(), new ItemStack(printerInkBlack, 1),
				"BBB",
				" Z ",
				'B', blackInk, 'Z', new ItemStack(printerInkBlack, 1, OreDictionary.WILDCARD_VALUE)).setRegistryName(OpenPrinter.MODID,"printer_ink_black_2"));
		
		event.getRegistry().register(new ShapedOreRecipe(folder.getRegistryName(), new ItemStack(folder, 1),
				"P P",
				" P ",
				'P', paper).setRegistryName(OpenPrinter.MODID,"folder"));
		
	}
}
