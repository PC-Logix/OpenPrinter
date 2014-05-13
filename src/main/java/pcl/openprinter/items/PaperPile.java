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
public class PaperPile extends Item{

	public PaperPile() {
		super();
		maxStackSize = 64;
		setUnlocalizedName("printerPaperPile");
		setTextureName("openprinter:paperPile");
		setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}

	public static void init() {
		PaperPile item = new PaperPile();
	}
}