package pcl.openprinter.items;

import pcl.openprinter.tileentity.FileCabinetTE;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockPos;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class ItemBlockFileCabinet extends ItemBlock {

	public ItemBlockFileCabinet(Block p_i45328_1_) {
		super(p_i45328_1_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ, IBlockState state) {
		if(super.placeBlockAt(stack, player, world, pos, side, hitX, hitY, hitZ, state))
        {
        	FileCabinetTE tileEntity = (FileCabinetTE) world.getTileEntity(pos);
        	if (stack.hasTagCompound()) {
            	tileEntity.name = stack.getTagCompound().getCompoundTag("display").getString("Name");
        	} 
        }
        return true;
    }
}
