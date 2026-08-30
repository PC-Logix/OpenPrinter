package pcl.openprinter;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.ItemStackHandler;

public final class StorageBlockEntity extends BlockEntity implements InventoryDevice {
    private final ItemStackHandler inventory;

    public StorageBlockEntity(BlockPos pos, BlockState state, int size) {
        super(state.is(OpenPrinter.FILE_CABINET.get()) ? OpenPrinter.FILE_CABINET_BE.get() : OpenPrinter.BRIEFCASE_BE.get(), pos, state);
        inventory = new ItemStackHandler(size) {
            @Override
            public int getSlotLimit(int slot) {
                return state.is(OpenPrinter.FILE_CABINET.get()) ? 1 : super.getSlotLimit(slot);
            }

            @Override
            public boolean isItemValid(int slot, ItemStack stack) {
                if (!state.is(OpenPrinter.FILE_CABINET.get())) return true;
                return stack.is(OpenPrinter.PRINTED_PAGE.get()) || stack.is(OpenPrinter.FOLDER.get())
                        || stack.is(Items.WRITTEN_BOOK) || stack.is(Items.WRITABLE_BOOK)
                        || stack.is(Items.PAPER) || stack.is(Items.BOOK);
            }

            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }
        };
    }

    @Override
    public ItemStackHandler inventory() {
        return inventory;
    }

    @Override
    public BlockPos devicePosition() {
        return worldPosition;
    }

    @Override
    public DeviceBlock.Kind deviceKind() {
        return getBlockState().is(OpenPrinter.FILE_CABINET.get())
                ? DeviceBlock.Kind.FILE_CABINET
                : DeviceBlock.Kind.BRIEFCASE;
    }

    void loadFromPortable(ItemStack stack, HolderLookup.Provider provider) {
        CompoundTag root = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (root.contains("inventory")) inventory.deserializeNBT(provider, root.getCompound("inventory"));
    }

    void saveToPortable(ItemStack stack, HolderLookup.Provider provider) {
        CustomData.update(DataComponents.CUSTOM_DATA, stack,
                root -> root.put("inventory", inventory.serializeNBT(provider)));
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put("inventory", inventory.serializeNBT(provider));
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains("inventory")) inventory.deserializeNBT(provider, tag.getCompound("inventory"));
    }
}
