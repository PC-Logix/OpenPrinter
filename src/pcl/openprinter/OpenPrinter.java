/**
 * 
 */
package pcl.openprinter;

/**
 * @author Caitlyn
 *
 */
import pcl.openprinter.blocks.Printer;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.gui.PrinterGUIHandler;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.items.PrinterInkBlack;
import pcl.openprinter.items.PrinterInkColor;
import pcl.openprinter.items.PrinterPaper;
import pcl.openprinter.items.PrinterPaperRoll;
import net.minecraftforge.common.Configuration;
import net.minecraftforge.common.Property;
import net.minecraftforge.oredict.OreDictionary;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.network.NetworkRegistry;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import li.cil.oc.api.Blocks;
import li.cil.oc.api.CreativeTab;
import li.cil.oc.api.Items;

@Mod(modid=OpenPrinter.MODID, name="OpenPrinter", version="0.0.1")
@NetworkMod(clientSideRequired=true)
public class OpenPrinter {
	
	public static final String MODID = "openprinter";
	
		public static Block printerBlock;
		public static Item  printerPaper;
		public static Item  printedPage;
		public static Item  printerPaperRoll;
		public static Item  printerInkColor;
		public static Item  printerInkBlack;
		
        @Instance(value = MODID)
        public static OpenPrinter instance;
        
        @SidedProxy(clientSide="pcl.openprinter.ClientProxy", serverSide="pcl.openprinter.CommonProxy")
        public static CommonProxy proxy;
        public static Config cfg = null;
        public static boolean render3D = true;
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        	render3D = cfg.render3D;
        	NetworkRegistry.instance().registerGuiHandler(this, new PrinterGUIHandler());
        	GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");
        	
        	//Register Blocks
        	printerBlock = new Printer(cfg.printerBlockID, Material.iron);
    		GameRegistry.registerBlock(printerBlock, "openprinter.printer");
    		
        	
        	printerPaper = new PrinterPaper(cfg.printerPaperID);
    		GameRegistry.registerItem(printerPaper, "openprinter.printerPaper");
    		printerPaper.setUnlocalizedName("printerPaper");
    		
        	printerPaperRoll = new PrinterPaperRoll(cfg.printerPaperRollID);
    		GameRegistry.registerItem(printerPaperRoll, "openprinter.printerPaperRoll");
    		printerPaperRoll.setUnlocalizedName("printerPaperRoll");
        	
        	printerInkColor = new PrinterInkColor(cfg.printerInkColorID);
    		GameRegistry.registerItem(printerInkColor, "openprinter.printerInkColor");
    		printerInkColor.setUnlocalizedName("printerInkColor");
        	
        	printerInkBlack = new PrinterInkBlack(cfg.printerInkBlackID);
    		GameRegistry.registerItem(printerInkBlack, "openprinter.printerInkBlack");
    		printerInkBlack.setUnlocalizedName("printerInkBlack");
        	
    		printedPage = new PrintedPage(cfg.printedPageID);
    		GameRegistry.registerItem(printedPage, "openprinter.printedPage");
    		printedPage.setUnlocalizedName("printedPage");
        	
        	ItemStack nuggetIron = Items.IronNugget;
        	ItemStack redstone   = new ItemStack(Item.redstone);
        	ItemStack microchip  = Items.MicrochipTier1;
        	ItemStack pcb		 = Items.PrintedCircuitBoard;
        	
        	GameRegistry.addRecipe( new ItemStack(printerBlock, 1), 
        			"IRI",
        			"MPM",
        			"IRI",
        			'I', nuggetIron, 'R', redstone, 'M', microchip, 'P', pcb);
        	
        }
        
        @EventHandler
    	public void load(FMLInitializationEvent event)
    	{
    		proxy.registerRenderers();
    	}
}