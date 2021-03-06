package pcl.openprinter.tileentity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.play.server.SPacketUpdateTileEntity;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ITickable;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import pcl.openprinter.ContentRegistry;

import pcl.openprinter.items.PrintedPage;
import pcl.openprinter.util.ItemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public class ShredderTE extends TileEntity implements ITickable {
	private ItemStackHandler inventoryOutput = new ItemStackHandler(9);
	private ItemStackHandler inventoryInput = new ShredderInput();

	private int processingTime = 0;

	public ShredderTE() {}

	class ShredderInput extends ItemStackHandler {
		ShredderInput(){
			super(1);
		}

		@Override
		public boolean isItemValid(int slot, @Nonnull ItemStack stack){
			return stack.getItem() instanceof PrintedPage
				|| stack.getItem().equals(Items.WRITTEN_BOOK)
				|| stack.getItem().equals(Items.WRITABLE_BOOK)
				|| stack.getItem().equals(Items.PAPER)
				|| stack.getItem().equals(Items.BOOK)
				|| stack.getItem().getRegistryName().toString().equals("bibliocraft:bigbook");
		}

		@Override
		@Nonnull
		public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate){
			return isItemValid(slot, stack) ? super.insertItem(slot, stack, simulate) : stack;
		}
	}

	@Deprecated
	private void readOldInventoryFromNBT(NBTTagCompound nbt){
		if(!nbt.hasKey("Items"))
			return;

		NBTTagList var2 = nbt.getTagList("Items",nbt.getId());

		for (int var3 = 0; var3 < var2.tagCount(); ++var3){
			NBTTagCompound var4 = var2.getCompoundTagAt(var3);
			byte var5 = var4.getByte("Slot");

			if(var5 == 0)
				inventoryInput.setStackInSlot(0, new ItemStack(var4));
			else if((var5-1) < inventoryOutput.getSlots()){
				inventoryOutput.setStackInSlot(var5-1, new ItemStack(var4));
			}
		}
	}

	@Override
	public void readFromNBT(NBTTagCompound nbt) {
		super.readFromNBT(nbt);

		if(nbt.hasKey("inventoryOut") && nbt.hasKey("inventoryIn")) {
			inventoryOutput.deserializeNBT(nbt.getCompoundTag("inventoryOut"));
			inventoryInput.deserializeNBT(nbt.getCompoundTag("inventoryIn"));
		}
		else
			readOldInventoryFromNBT(nbt); // remove this when ported to higher minecraft versions
	}

	@Override
	@Nonnull
	public NBTTagCompound writeToNBT(NBTTagCompound nbt){
		nbt.setTag("inventoryOut", inventoryOutput.serializeNBT());
		nbt.setTag("inventoryIn", inventoryInput.serializeNBT());

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

	@Override
	public void update() {
		ItemStack inputStack = inventoryInput.getStackInSlot(0);

		if(inputStack.isEmpty())
			return;

		++this.processingTime;
		if (this.processingTime > 10) {
			int outCount = 1;

			if(inputStack.getItem().getRegistryName().toString().equals("bibliocraft:bigbook"))
				outCount = 11; // bigbook involves 11 paper sheets
			else if(inputStack.getItem().equals(Items.BOOK) || inputStack.getItem().equals(Items.WRITABLE_BOOK) || inputStack.getItem().equals(Items.WRITTEN_BOOK))
				outCount = 3;

			ItemStack left = new ItemStack(ContentRegistry.shreddedPaper, outCount);

			for (int slot = 0; slot < inventoryOutput.getSlots(); slot++) {
				left = inventoryOutput.insertItem(slot, left, false);
				if(left.isEmpty())
					break;
			}
			inventoryInput.extractItem(0, 1, false);
			this.processingTime = 0;
		}
	}

	public void removed(){
		ItemUtils.dropItems(inventoryInput, world, getPos(), true, 10);
		ItemUtils.dropItems(inventoryOutput, world, getPos(), true, 10);
	}

	@Override
	public boolean hasCapability(@Nonnull Capability<?> capability, EnumFacing facing) {
		return (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) ||  super.hasCapability(capability, facing);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getCapability(@Nonnull Capability<T> capability, EnumFacing facing) {
		if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
			if(EnumFacing.UP.equals(facing))
				return (T) inventoryInput;
			else
				return (T) inventoryOutput;
		}

		return super.getCapability(capability, facing);
	}
}
