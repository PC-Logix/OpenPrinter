package pcl.openprinter.blocks;

import li.cil.oc.common.block.property.PropertyRotatable;
import net.minecraft.block.BlockContainer;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import pcl.openprinter.OpenPrinter;
import pcl.openprinter.tileentity.BriefcaseTE;
import pcl.openprinter.util.AABBHelper;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BlockBriefcase extends BlockContainer {
    private static final AxisAlignedBB emptyBB = new AxisAlignedBB(0, 0, 0, 0, 0, 0);
    private static final AxisAlignedBB boundingBox = new AxisAlignedBB(1f/16f, 0, 1f/16f * 6, 1f/16f*15, 1f/16f * 3, 1);

    public BlockBriefcase() {
        super(Material.CLOTH);
        setCreativeTab(OpenPrinter.CreativeTab);
        setTranslationKey("openprinter.briefcase");
        setHardness(.5f);
    }

    @Override
    public EnumBlockRenderType getRenderType(IBlockState state) {
        return EnumBlockRenderType.MODEL;
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer player, EnumHand hand, EnumFacing side, float hitX, float hitY, float hitZ) {
        TileEntity tileEntity = world.getTileEntity(pos);
        if (tileEntity == null) {
            return false;
        }

        if(player.isSneaking()) {
            harvestBlock(world, player, pos, state, tileEntity, player.getHeldItem(hand));
            return true;
        }

        player.openGui(OpenPrinter.instance, 7, world, pos.getX(), pos.getY(), pos.getZ());
        return true;
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
        AxisAlignedBB bb = AABBHelper.rotateVertical(boundingBox, state.getValue(PropertyRotatable.Pitch()));
        return AABBHelper.rotateHorizontal(bb, state.getValue(PropertyRotatable.Yaw()));
    }

    @Override
    public TileEntity createNewTileEntity(World var1, int var2) {
        return new BriefcaseTE();
    }


    @Override
    public void getDrops(net.minecraft.util.NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune){
        ItemStack dropStack = new ItemStack(state.getBlock());

        TileEntity tile = world.getTileEntity(pos);
        if(tile != null) {
            ((BriefcaseTE) tile).saveToStack(dropStack);
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

        if(stack.hasTagCompound()) {
            TileEntity tile = worldIn.getTileEntity(pos);
            if(tile instanceof BriefcaseTE)
                ((BriefcaseTE) tile).loadFromStack(stack);
        }
    }

    @Override
    public @Nonnull ExtendedBlockState createBlockState() {
        return IOrientableBlock.createBlockState(this);
    }

    @Override
    public int getMetaFromState(IBlockState state) {
        return IOrientableBlock.getMetaFromState(state);
    }

    @Override
    @Deprecated
    public @Nonnull IBlockState getStateFromMeta(int meta) {
        return IOrientableBlock.getStateFromMeta(getDefaultState(), meta);
    }

    @Override
    public @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        return IOrientableBlock.getExtendedState(state, world, pos);
    }

    @Override
    public @Nonnull IBlockState getStateForPlacement(@Nonnull World world, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, float hitX, float hitY, float hitZ, int meta, @Nonnull EntityLivingBase placer, EnumHand hand){
        EnumFacing yaw = IOrientableBlock.getYawForPlacement(placer, pos, facing);
        EnumFacing pitch = facing.equals(EnumFacing.UP) ? EnumFacing.NORTH : EnumFacing.UP;

        if(pitch.equals(EnumFacing.UP))
            yaw = yaw.getOpposite();

        IBlockState state = getDefaultState();

        state = state.withProperty(PropertyRotatable.Pitch(), pitch);
        state = state.withProperty(PropertyRotatable.Yaw(), yaw);

        return state;
    }

}
