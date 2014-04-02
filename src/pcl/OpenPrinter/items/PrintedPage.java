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
public class PrintedPage extends Item{

	public PrintedPage(int par1) {
		super(par1);
		maxStackSize = 64;
	}

	public static void init(int itemID) {
		PrintedPage item = new PrintedPage(itemID);
		item.setTextureName("minecraft:paper");
		GameRegistry.registerItem(item, "openprinter.printedPage");
		item.setUnlocalizedName("printedPage");
	}
}