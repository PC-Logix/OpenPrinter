/**
 * 
 */
package pcl.OpenPrinter.items;

import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
/**
 * @author Caitlyn
 *
 */
public class PrinterPaperRoll extends Item {

	/**
	 * 
	 */
	public PrinterPaperRoll(int par1) {
		super(par1);
		maxStackSize = 64;
	}

	public static void init(int itemID) {
		PrinterPaperRoll item = new PrinterPaperRoll(itemID);
		item.setTextureName("minecraft:paper");
		GameRegistry.registerItem(item, "openprinter.printerPaperRoll");
		item.setUnlocalizedName("printerPaperRoll");
		item.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}
	
}
