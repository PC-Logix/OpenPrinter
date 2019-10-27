package pcl.openprinter.blocks;

import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.properties.PropertyDirection;
import net.minecraft.block.state.IBlockState;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.PrinterTE;
import pcl.openprinter.util.AABBHelper;

import javax.annotation.Nullable;

public class BlockPrinter extends BlockContainer {
	private static final AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
	private static final AxisAlignedBB boundingBox = new AxisAlignedBB(0, 0, 0, 1, 1, 0.6);

	public BlockPrinter() {
		super(Material.IRON );
		setCreativeTab(OpenPrinter.CreativeTab);
		setTranslationKey("openprinter.printer");
		setHardness(.5f);
	}

	@Override
	public EnumBlockRenderType getRenderType(IBlockState state) {
	  return EnumBlockRenderType.MODEL;
	}
	
	@Override
	public void breakBlock (World world, BlockPos pos, IBlockState state) {
		PrinterTE tileEntity = (PrinterTE) world.getTileEntity(pos);
		tileEntity.removed();
		super.breakBlock(world, pos, state);
	}

	@Override
	public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
		TileEntity tileEntity = world.getTileEntity(pos);
		if (tileEntity == null || player.isSneaking()) {
			return false;
		}
	
		player.openGui(OpenPrinter.instance, 0, world, pos.getX(), pos.getY(), pos.getZ());
		return true;
	}

	public static final PropertyDirection PROPERTYFACING = PropertyDirection.create("facing", EnumFacing.Plane.HORIZONTAL);

	@Override
	public IBlockState getStateFromMeta(int meta)
	{
		EnumFacing facing = EnumFacing.byHorizontalIndex(meta);
		return this.getDefaultState().withProperty(PROPERTYFACING, facing);
	}

	@Override
	@Deprecated
	public boolean isOpaqueCube(IBlockState blockState) {
		return false;
	}

	@Override
	@Deprecated
	public boolean isFullCube(IBlockState blockState) {
		return false;
	}


	@Override
	@SideOnly(Side.CLIENT)
	public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos){
		return Minecraft.getMinecraft().player.isSneaking() ? super.getSelectedBoundingBox(state, worldIn, pos) : emptyBB;
	}

	@Deprecated
	@Override
	public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos){
		EnumFacing facing = state.getValue(PROPERTYFACING);
		return AABBHelper.rotateHorizontal(boundingBox, facing);
	}

	@Override
	public int getMetaFromState(IBlockState state) {
		EnumFacing facing = state.getValue(PROPERTYFACING);
		int facingbits = facing.getHorizontalIndex();
		return facingbits;
	}

	@Override
	public IBlockState getStateForPlacement(World world, BlockPos pos, EnumFacing facing, float hitX, float hitY, float hitZ, int meta, EntityLivingBase placer, EnumHand hand) {
		return super.getStateForPlacement(world, pos, facing, hitX, hitY, hitZ, meta, placer, hand).withProperty(PROPERTYFACING, placer.getHorizontalFacing());
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
	public TileEntity createNewTileEntity(World var1, int var2) {
		return new PrinterTE();
	}


	@Override
	public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
		ItemStack dropStack = new ItemStack(state.getBlock());

		NBTTagCompound nbt = new NBTTagCompound();

		TileEntity tile = world.getTileEntity(pos);

		if(tile != null) {
			nbt.setUniqueId("uuid", ((PrinterTE) tile).getUniqueId());
			dropStack.setTagCompound(nbt);
		}

		drops.add(dropStack);
	}

	@Override
	public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)    {
		if (willHarvest) return true; //If it will harvest, delay deletion of the block until after getDrops
		return super.removedByPlayer(state, world, pos, player, willHarvest);
	}
	/**
	 * Spawns the block's drops in the world. By the time this is called the Block has possibly been set to air via
	 * Block.removedByPlayer
	 */
	@Override
	public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool)    {
		super.harvestBlock(world, player, pos, state, te, tool);
		world.setBlockToAir(pos);
	}

	@Override
	public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack){
		super.onBlockPlacedBy(worldIn, pos, state, placer, stack);

		if(worldIn.isRemote)
			return;

		if(stack.hasTagCompound() && stack.getTagCompound().hasUniqueId("uuid")) {
			TileEntity tile = worldIn.getTileEntity(pos);
			if(tile instanceof PrinterTE)
				((PrinterTE) tile).setUniqueId(stack.getTagCompound().getUniqueId("uuid"));
		}
	}

}
