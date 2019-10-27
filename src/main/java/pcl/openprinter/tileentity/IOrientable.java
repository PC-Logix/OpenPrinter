package pcl.openprinter.tileentity;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.EnumFacing;

public interface IOrientable {
    default EnumFacing facing(){
        return pitch().getAxis().equals(EnumFacing.Axis.Y) ? pitch() : yaw();
    }

    EnumFacing yaw();
    EnumFacing pitch();

    void setPitch(EnumFacing pitchIn);
    void setYaw(EnumFacing yawIn);

    default void readFacingFromNBT(NBTTagCompound nbt){
        setYaw(EnumFacing.values()[nbt.getInteger("ocd:yaw")]);
        setPitch(EnumFacing.values()[nbt.getInteger("ocd:pitch")]);
    }

    default NBTTagCompound writeFacingToNBT(NBTTagCompound nbt){
        nbt.setInteger("ocd:yaw", yaw().ordinal());
        nbt.setInteger("ocd:pitch", pitch().ordinal());
        return nbt;
    }


}
