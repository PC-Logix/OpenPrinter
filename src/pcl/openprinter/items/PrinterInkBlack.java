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
public class PrinterInkBlack extends Item {

	public PrinterInkBlack(int par1) {
		super(par1);
		maxStackSize = 64;
	}

	public static void init(int itemID) {
		PrinterInkBlack item = new PrinterInkBlack(itemID);
		item.setTextureName("openprinter:PrinterInkBlack");
		GameRegistry.registerItem(item, "openprinter.printerInkBlack");
		item.setUnlocalizedName("printerInkBlack");
		item.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}
}