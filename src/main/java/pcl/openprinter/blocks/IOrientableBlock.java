package pcl.openprinter.blocks;

import li.cil.oc.common.block.property.PropertyRotatable;
import li.cil.oc.common.block.property.PropertyTile;
import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.common.property.ExtendedBlockState;
import net.minecraftforge.common.property.IExtendedBlockState;
import net.minecraftforge.common.property.IUnlistedProperty;
import pcl.openprinter.tileentity.IOrientable;

import javax.annotation.Nonnull;

public interface IOrientableBlock {
    static @Nonnull
    ExtendedBlockState createBlockState(Block block) {
        return new ExtendedBlockState(block, ((new IProperty[]{PropertyRotatable.Pitch(), PropertyRotatable.Yaw()})), ((new IUnlistedProperty[]{PropertyTile.Tile()})));
    }

    static @Nonnull
    IBlockState getStateForPlacement(IBlockState state, @Nonnull BlockPos pos, @Nonnull EnumFacing facing, @Nonnull EntityLivingBase placer){
        EnumFacing yaw = getYawForPlacement(placer, pos, facing);
        EnumFacing pitch = getPitchForPlacement(placer, pos, facing);

        state = state.withProperty(PropertyRotatable.Pitch(), pitch);
        state = state.withProperty(PropertyRotatable.Yaw(), yaw);

        return state;
    }

    static int getMetaFromState(IBlockState state) {
        return state.getValue(PropertyRotatable.Pitch()).ordinal() << 2 | state.getValue(PropertyRotatable.Yaw()).getHorizontalIndex();
    }

    @Deprecated
    static @Nonnull IBlockState getStateFromMeta(IBlockState state, int meta) {
        return state.withProperty(PropertyRotatable.Pitch(), EnumFacing.byIndex(meta >> 2)).withProperty(PropertyRotatable.Yaw(), EnumFacing.byHorizontalIndex(meta & 3));
    }

    static @Nonnull IBlockState getExtendedState(@Nonnull IBlockState state, IBlockAccess world, BlockPos pos) {
        TileEntity tile = world.getTileEntity(pos);

        if (state instanceof IExtendedBlockState && tile instanceof IOrientable) {
            IOrientable tileOrientable = (IOrientable) tile;
            return ((IExtendedBlockState) state)
                    .withProperty(PropertyTile.Tile(), tile)
                    .withProperty(PropertyRotatable.Pitch(), tileOrientable.pitch())
                    .withProperty(PropertyRotatable.Yaw(), tileOrientable.yaw());
        }

        return state;
    }

    static EnumFacing getYawForPlacement(EntityLivingBase entity, BlockPos blockPos, EnumFacing faceHit){
        EnumFacing yaw = EnumFacing.getDirectionFromEntityLiving(blockPos, entity);

        if(!faceHit.getAxis().equals(EnumFacing.Axis.Y))
            yaw = faceHit;

        if(yaw.getAxis().equals(EnumFacing.Axis.Y))
            yaw = EnumFacing.fromAngle(entity.rotationYaw).getOpposite();

        return yaw;
    }

    static EnumFacing getPitchForPlacement(EntityLivingBase entity, BlockPos blockPos, EnumFacing faceHit){
        EnumFacing pitch = EnumFacing.getDirectionFromEntityLiving(blockPos, entity);
        return pitch.getAxis().equals(EnumFacing.Axis.Y) ? pitch : EnumFacing.NORTH;
    }

}
