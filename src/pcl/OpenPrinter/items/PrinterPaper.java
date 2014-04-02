/**
 * 
 */
package pcl.OpenPrinter.items;

import pcl.OpenPrinter.Blocks.Printer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author Caitlyn
 *
 */
public class PrinterPaper extends Item{

	public PrinterPaper(int par1) {
		super(par1);
		maxStackSize = 64;
	}

	public static void init(int itemID) {
		PrinterPaper item = new PrinterPaper(itemID);
		item.setTextureName("minecraft:paper");
		GameRegistry.registerItem(item, "openprinter.printerPaper");
		item.setUnlocalizedName("printerPaper");
		item.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}
}