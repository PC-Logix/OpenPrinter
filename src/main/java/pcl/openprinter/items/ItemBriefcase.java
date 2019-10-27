package pcl.openprinter.items;

import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ActionResult;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.inventory.BriefcaseInventory;

public class ItemBriefcase extends ItemBlock {

    public ItemBriefcase(Block blockIn) {
        super(blockIn);
        setCreativeTab(OpenPrinter.CreativeTab);
        setTranslationKey("openprinter.briefcase");
        setMaxStackSize(1);
    }

    @Override
    public int getMaxItemUseDuration(ItemStack stack) {
        return 1;
    }

    @Override
    public EnumActionResult onItemUse(EntityPlayer player, World world, BlockPos pos, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ){
        if(player.isSneaking())
            return super.onItemUse(player, world, pos, hand, facing, hitX, hitY, hitZ);

        if(!world.isRemote)
            player.openGui(OpenPrinter.instance, 6, world, 0, 0, 0);

        return EnumActionResult.SUCCESS;
    }

    @Override
    public ActionResult<ItemStack> onItemRightClick(World world, EntityPlayer player, EnumHand hand) {
        if(!world.isRemote)
            player.openGui(OpenPrinter.instance, 6, world, 0, 0, 0);

        return new ActionResult<>(EnumActionResult.SUCCESS, player.getHeldItem(hand));
    }

    public static NBTTagCompound getInventoryFromStack(ItemStack stack){
        BriefcaseInventory inventory = new BriefcaseInventory(18);

        if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        if(!stack.getTagCompound().hasKey("inventory"))
            stack.getTagCompound().setTag("inventory", inventory.serializeNBT());

        return stack.getTagCompound().getCompoundTag("inventory");
    }

    public static ItemStack saveInventoryToStack(ItemStack stack, BriefcaseInventory inventory){
        if(!stack.hasTagCompound())
            stack.setTagCompound(new NBTTagCompound());

        stack.getTagCompound().setTag("inventory", inventory.serializeNBT());

        return stack;
    }

}
