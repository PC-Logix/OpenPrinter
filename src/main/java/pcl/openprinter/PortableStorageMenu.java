package pcl.openprinter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class PortableStorageMenu extends AbstractContainerMenu {
    private final ItemStack containerStack;
    private final PortableInventory portableInventory;
    private final int portableSlots;
    private final int lockedPlayerSlot;

    public PortableStorageMenu(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, buffer.readEnum(InteractionHand.class), buffer.readVarInt());
    }

    public PortableStorageMenu(int id, Inventory playerInventory, InteractionHand hand, int size) {
        super(OpenPrinter.PORTABLE_MENU.get(), id);
        containerStack = playerInventory.player.getItemInHand(hand);
        portableSlots = size;
        portableInventory = new PortableInventory(containerStack, size, playerInventory.player.registryAccess(),
                containerStack.is(OpenPrinter.FOLDER.get()));
        lockedPlayerSlot = hand == InteractionHand.MAIN_HAND ? playerInventory.selected : -1;
        addPortableSlots();
        addPlayerSlots(playerInventory);
    }

    private void addPortableSlots() {
        int columns = 9;
        for (int slot = 0; slot < portableSlots; slot++) {
            int top = portableSlots == 18 ? 17 : 19;
            addSlot(new SlotItemHandler(portableInventory, slot, 8 + (slot % columns) * 18, top + (slot / columns) * 18));
        }
    }

    private void addPlayerSlots(Inventory inventory) {
        int top = portableSlots == 18 ? 71 : 55;
        for (int row = 0; row < 3; row++) for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, top + row * 18));
        }
        for (int column = 0; column < 9; column++) {
            final int inventorySlot = column;
            addSlot(new Slot(inventory, column, 8 + column * 18, top + 58) {
                @Override public boolean mayPickup(Player player) { return inventorySlot != lockedPlayerSlot; }
            });
        }
    }

    boolean isBriefcase() {
        return portableSlots == 18;
    }

    @Override
    public boolean stillValid(Player player) {
        return !containerStack.isEmpty() && player.getInventory().contains(containerStack);
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (index < portableSlots) {
            if (!moveItemStackTo(original, portableSlots, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(original, 0, portableSlots, false)) return ItemStack.EMPTY;
        if (original.isEmpty()) slot.set(ItemStack.EMPTY); else slot.setChanged();
        return copy;
    }
}
