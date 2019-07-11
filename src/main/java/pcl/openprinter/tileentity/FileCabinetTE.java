package pcl.openprinter.tileentity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.openprinter.items.ItemFolder;
import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.util.ItemUtils;

import javax.annotation.Nonnull;

public class FileCabinetTE extends TileEntity {
	private ItemStackHandler inventory = new FileCabinetInventory();

	public FileCabinetTE() {}

	class FileCabinetInventory extends ItemStackHandler {
		FileCabinetInventory(){
			super(30);
		}

		@Override
		public int getSlotLimit(int slot){
			return 1;
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return     stack.getItem() instanceof PrintedPage
					|| stack.getItem() instanceof ItemFolder
					|| stack.getItem().equals(Items.WRITTEN_BOOK)
					|| stack.getItem().equals(Items.WRITABLE_BOOK)
					|| stack.getItem().equals(Items.PAPER)
					|| stack.getItem().equals(Items.BOOK);
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	}

	@Deprecated
	public void readOldInventoryFromNBT(NBTTagCompound par1NBTTagCompound) {
		if(!par1NBTTagCompound.hasKey("Items"))
			return;

		NBTTagList var2 = par1NBTTagCompound.getTagList("Items", par1NBTTagCompound.getId());
		for (int var3 = 0; var3 < var2.tagCount(); ++var3) {
			NBTTagCompound var4 = (NBTTagCompound) var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");
			if (var5 >= 0 && var5 < inventory.getSlots()) {
				inventory.setStackInSlot(var5, new ItemStack(var4));
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt){
		super.readFromNBT(nbt);
		if(nbt.hasKey("inventory"))
			inventory.deserializeNBT(nbt.getCompoundTag("inventory"));
		else
			readOldInventoryFromNBT(nbt); // remove this when porting upwards
	}

	@Override
	@Nonnull
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setTag("inventory", inventory.serializeNBT());
		return super.writeToNBT(nbt);
	}

	public void removed(){
		ItemUtils.dropItems(inventory, world, getPos(), true, 10);
	}

	@Override
	@Nonnull
	public NBTTagCompound getUpdateTag() {
		NBTTagCompound tag = new NBTTagCompound();
		this.writeToNBT(tag);
		return tag;
	}

	@Override
	public void onDataPacket(NetworkManager net, SPacketUpdateTileEntity packet) {
		readFromNBT(packet.getNbtCompound());
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||  super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			return (T) inventory;
		}

		return super.getCapability(capability, facing);
	}
}
