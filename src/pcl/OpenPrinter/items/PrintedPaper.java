/**
 * 
 */
package pcl.OpenPrinter.items;

import pcl.OpenPrinter.Blocks.BlockPrinter;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author Caitlyn
 *
 */
public class PrintedPaper extends Item{

	public PrintedPaper(int par1) {
		super(par1);
		maxStackSize = 64;
	}

	public static void init(int itemID) {
		PrintedPaper item = new PrintedPaper(itemID);
		item.setTextureName("minecraft:paper");
		GameRegistry.registerItem(item, "openprinter.printedPage");
		LanguageRegistry.addName(item, "OC Printed Page");
	}

}
