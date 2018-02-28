package pcl.openprinter.blocks;

import java.util.Random;

import javax.annotation.Nullable;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.ShredderTE;

public class BlockShredder extends BlockContainer {
	private Random random;
	
	public BlockShredder() {
		super(Material.IRON );
		setCreativeTab(OpenPrinter.CreativeTab);
		setUnlocalizedName("shredder");
		setHardness(.5f);

		random = new Random();
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
	  return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void breakBlock(World world, BlockPos pos, IBlockState state) {
		ShredderTE tileEntity = (ShredderTE) world.getTileEntity(pos);
		dropContent(tileEntity, world, tileEntity.getPos().getX(), tileEntity.getPos().getY(), tileEntity.getPos().getZ());
		super.breakBlock(world, pos, state);
	}

	public void dropContent(IInventory chest, World world, int xCoord, int yCoord, int zCoord) {
		if (chest == null)
			return;

		for (int i1 = 0; i1 < chest.getSizeInventory(); ++i1) {
			ItemStack itemstack = chest.getStackInSlot(i1);

			if (!itemstack.isEmpty()) {
				float offsetX = random.nextFloat() * 0.8F + 0.1F;
				float offsetY = random.nextFloat() * 0.8F + 0.1F;
				float offsetZ = random.nextFloat() * 0.8F + 0.1F;
				EntityItem entityitem;

				for (; itemstack.getCount() > 0; world.spawnEntity(entityitem)) {
					int stackSize = random.nextInt(21) + 10;
					if (stackSize > itemstack.getCount())
						stackSize = itemstack.getCount();

					itemstack.setCount(itemstack.getCount() - stackSize);
					ItemStack stack = new ItemStack(itemstack.getItem(), stackSize, itemstack.getItemDamage());
					if(itemstack.hasTagCompound()){
						stack.setTagCompound(itemstack.getTagCompound().copy());
					}
					entityitem = new EntityItem(world, (double)((float)xCoord + offsetX), (double)((float)yCoord + offsetY), (double)((float)zCoord + offsetZ), stack);

					float velocity = 0.05F;
					entityitem.motionX = (double)((float)random.nextGaussian() * velocity);
					entityitem.motionY = (double)((float)random.nextGaussian() * velocity + 0.2F);
					entityitem.motionZ = (double)((float)random.nextGaussian() * velocity);
				}
			}
		}
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
	
		player.openGui(OpenPrinter.instance, 2, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	public static final PropertyDirection PROPERTYFACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing = EnumFacing.getHorizontal(meta);
		return this.getDefaultState().withProperty(PROPERTYFACING, facing);
	}
	
	@Override
	public int getMetaFromState(IBlockState state)
	{
		EnumFacing facing = (EnumFacing)state.getValue(PROPERTYFACING);
		int facingbits = facing.getHorizontalIndex();
		return facingbits;
	}

	@Override
	public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
	{
		return state;
	}

	@Override
	protected BlockStateContainer createBlockState()
	{
		return new BlockStateContainer(this, new IProperty[] {PROPERTYFACING});
	}

	@Override
	public Item getItemDropped(IBlockState state, Random random, int fortune) {
		return null;
	}

	@Override
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new ShredderTE();
	}

}
