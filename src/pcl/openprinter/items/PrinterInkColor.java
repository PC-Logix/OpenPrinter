/**
 * 
 */
package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import cpw.mods.fml.common.registry.GameRegistry;
import net.minecraft.item.Item;

/**
 * @author Caitlyn
 *
 */
public class PrinterInkColor extends Item {

	public PrinterInkColor(int par1) {
		super(par1);
		maxStackSize = 1;
		this.setMaxDamage(OpenPrinter.cfg.printerInkUse);
		setNoRepair();
	}

	public static void init(int itemID) {
		PrinterInkColor item = new PrinterInkColor(itemID);
		item.setTextureName("openprinter:PrinterInkColor");
		item.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}
}