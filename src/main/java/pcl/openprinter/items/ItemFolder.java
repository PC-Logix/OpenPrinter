package pcl.openprinter.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.GuiFolderView;

public class ItemFolder extends Item {

	public ItemFolder() {
		super();
		setCreativeTab(OpenPrinter.CreativeTab);
		setUnlocalizedName("filefolder");
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(ItemStack itemstack, World world, EntityPlayer player, EnumHand hand) {
		if (!world.isRemote) {
			if (player.isSneaking()) {
				player.openGui(OpenPrinter.instance, 3, world, 0, 0, 0);
			}
		} else {
			if (!player.isSneaking()) {
				player.openGui(OpenPrinter.instance, 4, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
				GuiFolderView.stack = itemstack;
				return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
			}
		}

		return new ActionResult<ItemStack>(EnumActionResult.SUCCESS, itemstack);
	}
}
