package pcl.openprinter;

import net.minecraft.core.HolderLookup;
import net.minecraft.core.component.DataComponents;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.component.CustomData;
import net.neoforged.neoforge.items.ItemStackHandler;

final class PortableInventory extends ItemStackHandler {
    private final ItemStack container;
    private final HolderLookup.Provider provider;
    private final boolean folder;
    private boolean loading;

    PortableInventory(ItemStack container, int size, HolderLookup.Provider provider, boolean folder) {
        super(size);
        this.container = container;
        this.provider = provider;
        this.folder = folder;
        CompoundTag root = container.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (root.contains("inventory")) {
            loading = true;
            deserializeNBT(provider, root.getCompound("inventory"));
            loading = false;
        }
    }

    @Override
    public int getSlotLimit(int slot) {
        return folder ? 1 : super.getSlotLimit(slot);
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack) {
        if (!folder) return !(stack.getItem() instanceof PortableBlockItem);
        return stack.is(OpenPrinter.PRINTED_PAGE.get()) || stack.is(Items.WRITTEN_BOOK)
                || stack.is(Items.WRITABLE_BOOK) || stack.is(Items.PAPER) || stack.is(Items.BOOK);
    }

    @Override
    protected void onContentsChanged(int slot) {
        if (loading) return;
        CustomData.update(DataComponents.CUSTOM_DATA, container,
                tag -> tag.put("inventory", serializeNBT(provider)));
    }
}
