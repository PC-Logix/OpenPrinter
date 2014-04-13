/**
 * 
 */
package pcl.openprinter.items;

import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * @author Caitlyn
 *
 */
public class PrinterInkColor extends Item {

	public PrinterInkColor(int par1) {
		super(par1);
		maxStackSize = 64;
	}

	public static void init(int itemID) {
		PrinterInkColor item = new PrinterInkColor(itemID);
		item.setTextureName("openprinter:PrinterInkColor");
		GameRegistry.registerItem(item, "openprinter.printerInkColor");
		item.setUnlocalizedName("printerInkColor");
		item.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}
}