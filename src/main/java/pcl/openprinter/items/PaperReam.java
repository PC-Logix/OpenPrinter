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
public class PaperReam extends Item{

	public PaperReam() {
		super();
		maxStackSize = 64;
		setUnlocalizedName("printerPaperReam");
		setTextureName("openprinter:paperReam");
		setCreativeTab(li.cil.oc.api.CreativeTab.Instance);
	}

	public static void init() {
		PaperReam item = new PaperReam();
	}
}