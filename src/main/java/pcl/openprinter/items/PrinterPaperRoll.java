/**
 * 
 */
package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
/**
 * @author Caitlyn
 *
 */
public class PrinterPaperRoll extends Item {

	public PrinterPaperRoll() {
		super();
		maxStackSize = 1;
		this.setMaxDamage(256);
		setNoRepair();
		setUnlocalizedName("printerPaperRoll");
		setTextureName("openprinter:PrinterPaperRoll");
		setCreativeTab(OpenPrinter.CreativeTab);
	}

	@Override
	public void onUpdate(ItemStack stack, World world, Entity entity, int n,
			boolean b) {
		if (stack.getItemDamage() >= stack.getMaxDamage())
			stack.stackSize -= 1; // if this is reduced to 0, it is
									// automatically "destroyed"
	}
	
}
