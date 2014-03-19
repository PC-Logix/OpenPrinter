/**
 * 
 */
package pcl.OpenPrinter;

/**
 * @author Caitlyn
 *
 */
import pcl.OpenPrinter.Blocks.BlockPrinter;
import pcl.OpenPrinter.TileEntity.PrinterTE;
import pcl.OpenPrinter.items.PrintedPaper;
import net.minecraftforge.common.Configuration;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.creativetab.CreativeTabs;
import cpw.mods.fml.common.Mod;
import cpw.mods.fml.common.Mod.EventHandler;
import cpw.mods.fml.common.Mod.Instance;
import cpw.mods.fml.common.SidedProxy;
import cpw.mods.fml.common.event.FMLInitializationEvent;
import cpw.mods.fml.common.event.FMLPostInitializationEvent;
import cpw.mods.fml.common.event.FMLPreInitializationEvent;
import cpw.mods.fml.common.network.NetworkMod;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import li.cil.oc.api.CreativeTab;

@Mod(modid=OpenPrinter.MODID, name="OpenPrinter", version="0.0.1")
@NetworkMod(clientSideRequired=true)
public class OpenPrinter {
	
	public static final String MODID = "openprinter";
	
		public static Block printerBlock;
        @Instance(value = "OpenPrinter")
        public static OpenPrinter instance;
        
        @SidedProxy(clientSide="pcl.OpenPrinter.ClientProxy", serverSide="pcl.OpenPrinter.CommonProxy")
        public static CommonProxy proxy;
        public static Config cfg = null;
        
        @EventHandler
        public void preInit(FMLPreInitializationEvent event) {
        	cfg = new Config(new Configuration(event.getSuggestedConfigurationFile()));
        }
        
        @EventHandler
    	public void load(FMLInitializationEvent event)
    	{
        	BlockPrinter.init(cfg.blockID);
        	PrintedPaper.init(cfg.itemID);
        	GameRegistry.registerTileEntity(PrinterTE.class, "PrinterTE");
    		proxy.registerRenderers();
    	}
}