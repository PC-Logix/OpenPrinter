package pcl.openprinter.tileentity;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import pcl.openprinter.inventory.BriefcaseInventory;
import pcl.openprinter.items.ItemBriefcase;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class BriefcaseTE extends TileEntity implements IOrientable {
    EnumFacing yaw = EnumFacing.NORTH, pitch = EnumFacing.DOWN;

    BriefcaseInventory inventory = new BriefcaseInventory(18);

    public EnumFacing yaw(){
        return this.yaw;
    }

    public EnumFacing pitch(){
        return this.pitch;
    }

    public void setPitch(EnumFacing pitchIn){
        this.pitch = pitchIn;
    }

    public void setYaw(EnumFacing yawIn){
        this.yaw = yawIn;
    }

    @Override
    public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
        return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||  super.hasCapability(capability, facing);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
            return (T) inventory;
        else
            return super.getCapability(capability, facing);
    }

    @Override
    public void readFromNBT(NBTTagCompound nbt) {
        super.readFromNBT(nbt);

        if(nbt.hasKey("inventory"))
            inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
    }

    @Override
    @Nonnull
    public NBTTagCompound writeToNBT(NBTTagCompound nbt){
        nbt.setTag("inventory", inventory.serializeNBT());

        return super.writeToNBT(nbt);
    }

    @Override
    @Nonnull
    public NBTTagCompound getUpdateTag() {
        NBTTagCompound tag = new NBTTagCompound();
        this.writeToNBT(tag);
        return tag;
    }

    @Nullable
    @Override
    public SPacketUpdateTileEntity getUpdatePacket() {
        return new SPacketUpdateTileEntity(getPos(), 0, getUpdateTag());
    }

    @Override
    @SideOnly(Side.CLIENT)
    public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
        handleUpdateTag(packet.getNbtCompound());
    }

    public ItemStack saveToStack(ItemStack stack){
        return ItemBriefcase.saveInventoryToStack(stack, inventory);
    }

    public void loadFromStack(ItemStack stack){
        inventory.deserializeNBT(ItemBriefcase.getInventoryFromStack(stack));
    }
}
