package pcl.openprinter.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumHand;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.text.TextComponentString;

import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;

public class ItemFolder extends Item {

	public ItemFolder() {
		super();
		setCreativeTab(OpenPrinter.CreativeTab);
		setTranslationKey("openprinter.folder");
	}
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

	@Override
	public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
		ItemStack folder = player.getHeldItem(hand);
		// "Other hand" (where the dye should be)
		EnumHand otherHand = (hand == EnumHand.MAIN_HAND) ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND;
		ItemStack dye = player.getHeldItem(otherHand);

		if (!world.isRemote) {
			// SERVER: handle coloring while sneaking
			if (player.isSneaking() && !dye.isEmpty() && dye.getItem() == Items.DYE) {
				EnumDyeColor d = EnumDyeColor.byDyeDamage(dye.getMetadata()); // 1.12: 0..15
				int rgb = d.getColorValue();

				setFolderColor(folder, rgb);

				if (!player.capabilities.isCreativeMode) dye.shrink(1);
				player.sendMessage(new TextComponentString(
						String.format("Folder dyed %s (#%06X)", d.getName(), rgb)
				));
				return new ActionResult<>(EnumActionResult.SUCCESS, folder);
			}

			// SERVER: your existing sneaking behavior if no dye present
			if (player.isSneaking()) {
				player.openGui(OpenPrinter.instance, 3, world, 0, 0, 0);
				return new ActionResult<>(EnumActionResult.SUCCESS, folder);
			}
			// Non-sneaking: let client open its GUI path below
			return new ActionResult<>(EnumActionResult.SUCCESS, folder);
		} else {
			// CLIENT: your existing non-sneak GUI open
			if (!player.isSneaking()) {
				player.openGui(OpenPrinter.instance, 4, player.getEntityWorld(),
						(int) player.posX, (int) player.posY, (int) player.posZ);
				return new ActionResult<>(EnumActionResult.SUCCESS, folder);
			}
			return new ActionResult<>(EnumActionResult.SUCCESS, folder);
		}
	}


	public static final int DEFAULT_MANILA = 0xD2B48C;

	public static int getFolderColor(ItemStack stack) {
		if (stack == null || stack.isEmpty()) return DEFAULT_MANILA;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) return DEFAULT_MANILA;
		NBTTagCompound root = tag.getCompoundTag("OpenPrinter");
		return root.hasKey("FolderColor") ? root.getInteger("FolderColor") : DEFAULT_MANILA;
	}

	public static void setFolderColor(ItemStack stack, int rgb) {
		if (stack == null || stack.isEmpty()) return;
		NBTTagCompound tag = stack.getTagCompound();
		if (tag == null) tag = new NBTTagCompound();
		NBTTagCompound root = tag.getCompoundTag("OpenPrinter");
		root.setInteger("FolderColor", rgb & 0xFFFFFF);
		tag.setTag("OpenPrinter", root);
		stack.setTagCompound(tag);
	}
}
