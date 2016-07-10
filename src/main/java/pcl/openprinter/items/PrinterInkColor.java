/**
 * 
 */
package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import net.minecraftforge.fml.common.registry.GameRegistry;
import net.minecraft.entity.Entity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class PrinterInkColor extends Item {

	public PrinterInkColor() {
		super();
		maxStackSize = 1;
		this.setMaxDamage(OpenPrinter.cfg.printerInkUse);
		setNoRepair();
		setUnlocalizedName("printerInkColor");
		//setTextureName("openprinter:PrinterInkColor");
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