/**
 * 
 */
package pcl.openprinter.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.PaperGUI;

/**
 * @author Caitlyn
 *
 */
public class PrintedPage extends Item{

	public PrintedPage() {
		super();
		maxStackSize = 1;
		setCreativeTab(OpenPrinter.CreativeTab);
		setUnlocalizedName("openprinter.printed_page");
		//setTextureName("minecraft:paper");
	}
	
	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack itemstack = player.getHeldItem(hand);
		if (!world.isRemote) {
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		} else {
			player.openGui(OpenPrinter.instance, 1, player.getEntityWorld(), (int) player.posX, (int) player.posY, (int) player.posZ);
			PaperGUI.stack = itemstack;
			return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
		}
	}
}
