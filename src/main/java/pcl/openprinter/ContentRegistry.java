package pcl.openprinter;

import pcl.openprinter.blocks.BlockFileCabinet;
import pcl.openprinter.blocks.BlockPrinter;
import pcl.openprinter.blocks.BlockShredder;
import pcl.openprinter.items.ItemFolder;
import pcl.openprinter.items.ItemPaperShreds;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.tileentity.FileCabinetTE;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.tileentity.ShredderTE;

import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;

public class ContentRegistry {
	public static Block printerBlock;
	public static Block shredderBlock;
	public static Block fileCabinetBlock;
	public static Item  printedPage;
	//public static Item  printerPaperRoll;
	public static Item  printerInkColor;
	public static Item  printerInkBlack;
	public static Item  shreddedPaper;
	public static Item  folder;

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
		
		//printerPaperRoll = new PrinterPaperRoll();
		//register.getRegistry().register(init(printerPaperRoll, "printer_paper_roll"));
		
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


	public static Block init(Block block, String name) {
		return block.setUnlocalizedName(name).setRegistryName("openprinter:" + name);
	}

}
