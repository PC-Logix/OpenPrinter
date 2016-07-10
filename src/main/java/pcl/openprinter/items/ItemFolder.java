package pcl.openprinter.items;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.gui.GuiFolderView;

public class ItemFolder extends Item {

	public ItemFolder() {
		super();
		setCreativeTab(OpenPrinter.CreativeTab);
		setUnlocalizedName("filefolder");
		setTextureName(OpenPrinter.MODID + ":folderEmpty");
	}

<<<<<<< HEAD
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}

=======
	@SideOnly(Side.CLIENT)
	private static IIcon itemIcon2;
	@SideOnly(Side.CLIENT)
	private static IIcon itemIcon1;
	
	@Override
	public int getMaxItemUseDuration(ItemStack stack) {
		return 1;
	}
	
/*	@SideOnly(Side.CLIENT)
	@Override
	public void registerIcons(IIconRegister iconRegister)
	{
		itemIcon1 = iconRegister.registerIcon("openprinter:folderEmpty");   
		itemIcon2 = iconRegister.registerIcon("openprinter:folderFull");
	}

	@Override
    @SideOnly(Side.CLIENT)
    public boolean requiresMultipleRenderPasses()
    {
        return true;
    }
	
	@Override
    @SideOnly(Side.CLIENT)
    public int getRenderPasses(int metadata)
    {
        return requiresMultipleRenderPasses() ? 2 : 1;
    }
	
	@SideOnly(Side.CLIENT)
	@Override
	public IIcon getIcon(ItemStack stack, int renderPass){
		if(stack.hasTagCompound()){
			if (stack.getTagCompound().hasKey("ItemInventory", Constants.NBT.TAG_LIST) && stack.getTagCompound().getTagList("ItemInventory", Constants.NBT.TAG_LIST).tagCount() > 0)
			return itemIcon2;
		}
		return itemIcon1;
	}
*/
>>>>>>> origin/1.8
	@Override
	public ItemStack onItemRightClick(ItemStack itemstack, World world, EntityPlayer player)
	{
		if (!world.isRemote) {
			if (player.isSneaking()) {
				player.openGui(OpenPrinter.instance, 3, world, 0, 0, 0);
			}
		} else {
			if (!player.isSneaking()) {
				player.openGui(OpenPrinter.instance, 4, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
				GuiFolderView.stack = itemstack;
				return itemstack;
			}
		}

		return itemstack;
	}
}
