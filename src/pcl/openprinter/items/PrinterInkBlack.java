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
public class PrinterInkBlack extends Item {

	public PrinterInkBlack(int par1) {
		super(par1);
		maxStackSize = 1;
		maxStackSize = 1;
		this.setMaxDamage(OpenPrinter.cfg.printerInkUse);
		setNoRepair();
	}

	public static void init(int itemID) {
		PrinterInkBlack item = new PrinterInkBlack(itemID);
		item.setTextureName("openprinter:PrinterInkBlack");
		item.setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}
}