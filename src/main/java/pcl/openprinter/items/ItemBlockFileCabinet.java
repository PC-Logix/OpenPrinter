package pcl.openprinter.items;

import pcl.openprinter.tileentity.FileCabinetTE;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public class ItemBlockFileCabinet extends ItemBlock {

	public ItemBlockFileCabinet(Block p_i45328_1_) {
		super(p_i45328_1_);
		// TODO Auto-generated constructor stub
	}
	
	@Override
    public boolean placeBlockAt(ItemStack stack, EntityPlayer player, World world, int x, int y, int z, int side, float hitX, float hitY, float hitZ, int metadata)
    {
        if(super.placeBlockAt(stack, player, world, x, y, z, side, hitX, hitY, hitZ, metadata))
        {
        	FileCabinetTE tileEntity = (FileCabinetTE) world.getTileEntity(x, y, z);
        	if (stack.hasTagCompound()) {
            	tileEntity.name = stack.getTagCompound().getCompoundTag("display").getString("Name");
        	} 
        }
        return true;
    }
	
}
