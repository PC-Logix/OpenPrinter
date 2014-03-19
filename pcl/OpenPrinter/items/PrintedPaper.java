/**
 * 
 */
package pcl.OpenPrinter.items;

import pcl.OpenPrinter.Blocks.BlockPrinter;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.item.Item;

/**
 * @author Caitlyn
 *
 */
public class PrintedPaper extends Item{

	public PrintedPaper(int par1) {
		super(par1);
		// TODO Auto-generated constructor stub
	}

	public static void init(int itemID) {
		// TODO Auto-generated method stub
		PrintedPaper item = new PrintedPaper(itemID);
		GameRegistry.registerItem(item, "openprinter.printedPaper");
		LanguageRegistry.addName(item, "OC Printed Paper");
	}

}
