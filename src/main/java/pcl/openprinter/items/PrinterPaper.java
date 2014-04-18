/**
 * 
 */
package pcl.openprinter.items;

import pcl.openprinter.blocks.Printer;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;

/**
 * @author Caitlyn
 *
 */
public class PrinterPaper extends Item{

	public PrinterPaper() {
		super();
		maxStackSize = 64;
	}

	public static void init() {
		PrinterPaper item = new PrinterPaper();
	}
}