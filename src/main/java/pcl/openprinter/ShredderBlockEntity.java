package pcl.openprinter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public final class ShredderBlockEntity extends BlockEntity implements InventoryDevice {
    private final ItemStackHandler inventory = new ItemStackHandler(10) {
        @Override
        public boolean isItemValid(int slot, ItemStack stack) {
            if (slot != 0) return false;
            return stack.is(OpenPrinter.PRINTED_PAGE.get()) || stack.is(Items.WRITTEN_BOOK)
                    || stack.is(Items.WRITABLE_BOOK) || stack.is(Items.PAPER) || stack.is(Items.BOOK);
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private int processingTime;

    public ShredderBlockEntity(BlockPos pos, BlockState state) {
        super(OpenPrinter.SHREDDER_BE.get(), pos, state);
    }

    @Override
    public DeviceBlock.Kind deviceKind() {
        return DeviceBlock.Kind.SHREDDER;
    }

    void serverTick() {
        if (level == null || level.isClientSide) return;
        ItemStack input = inventory.getStackInSlot(0);
        if (input.isEmpty()) {
            processingTime = 0;
            return;
        }
        if (++processingTime <= 10) return;

        int count = input.is(Items.BOOK) || input.is(Items.WRITABLE_BOOK) || input.is(Items.WRITTEN_BOOK) ? 3 : 1;
        ItemStack remainder = insertOutput(new ItemStack(OpenPrinter.PAPER_SHREDS.get(), count));
        if (remainder.isEmpty()) {
            inventory.extractItem(0, 1, false);
            processingTime = 0;
            setChanged();
        }
    }

    private ItemStack insertOutput(ItemStack remainder) {
        for (int slot = 1; slot < inventory.getSlots() && !remainder.isEmpty(); slot++) {
            ItemStack existing = inventory.getStackInSlot(slot);
            if (existing.isEmpty()) {
                inventory.setStackInSlot(slot, remainder.copy());
                return ItemStack.EMPTY;
            }
            if (ItemStack.isSameItemSameComponents(existing, remainder)) {
                int moved = Math.min(remainder.getCount(), existing.getMaxStackSize() - existing.getCount());
                if (moved > 0) {
                    existing.grow(moved);
                    remainder.shrink(moved);
                    inventory.setStackInSlot(slot, existing);
                }
            }
        }
        return remainder;
    }

    @Override
    public ItemStackHandler inventory() {
        return inventory;
    }

    @Override
    public IItemHandler itemHandler(@Nullable Direction side) {
        return inventory;
    }

    @Override
    public BlockPos devicePosition() {
        return worldPosition;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", inventory.serializeNBT(provider));
        tag.putInt("processingTime", processingTime);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inventory")) inventory.deserializeNBT(provider, tag.getCompound("inventory"));
        processingTime = tag.getInt("processingTime");
    }
}
