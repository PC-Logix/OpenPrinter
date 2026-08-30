package pcl.openprinter;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class DeviceMenu extends AbstractContainerMenu {
    private final InventoryDevice device;
    private final DeviceBlock.Kind kind;
    private final int machineSlotCount;
    private final ContainerData printerData;

    public DeviceMenu(int id, Inventory playerInventory, FriendlyByteBuf buffer) {
        this(id, playerInventory, findDevice(playerInventory, buffer));
    }

    public DeviceMenu(int id, Inventory playerInventory, InventoryDevice device) {
        super(OpenPrinter.DEVICE_MENU.get(), id);
        this.device = device;
        this.kind = device.deviceKind();
        this.printerData = device instanceof PrinterBlockEntity printer ? printer.menuData() : new SimpleContainerData(0);
        this.machineSlotCount = addMachineSlots(device);
        addPlayerSlots(playerInventory);
        addDataSlots(printerData);
    }

    private static InventoryDevice findDevice(Inventory inventory, FriendlyByteBuf buffer) {
        if (inventory.player.level().getBlockEntity(buffer.readBlockPos()) instanceof InventoryDevice device) return device;
        throw new IllegalStateException("OpenPrinter menu opened without an inventory device");
    }

    private int addMachineSlots(InventoryDevice device) {
        return switch (kind) {
            case PRINTER -> {
                addSlot(new SlotItemHandler(device.inventory(), 0, 30, 47));
                addSlot(new SlotItemHandler(device.inventory(), 1, 60, 47));
                addSlot(new SlotItemHandler(device.inventory(), 2, 129, 47));
                addSlot(new SlotItemHandler(device.inventory(), 3, 94, 17));
                for (int slot = 4; slot < 13; slot++) {
                    addSlot(new SlotItemHandler(device.inventory(), slot, 8 + (slot - 4) * 18, 87));
                }
                yield 13;
            }
            case SHREDDER -> {
                addSlot(new SlotItemHandler(device.inventory(), 0, 79, 34));
                for (int slot = 1; slot < 10; slot++) {
                    addSlot(new SlotItemHandler(device.inventory(), slot, 8 + (slot - 1) * 18, 87));
                }
                yield 10;
            }
            case FILE_CABINET -> {
                for (int slot = 1; slot <= 27; slot++) {
                    int visibleSlot = slot - 1;
                    addSlot(new SlotItemHandler(device.inventory(), slot,
                            8 + (visibleSlot % 9) * 18, 15 + (visibleSlot / 9) * 18));
                }
                yield 27;
            }
            case BRIEFCASE -> {
                for (int slot = 0; slot < 18; slot++) {
                    addSlot(new SlotItemHandler(device.inventory(), slot,
                            8 + (slot % 9) * 18, 17 + (slot / 9) * 18));
                }
                yield 18;
            }
        };
    }

    private void addPlayerSlots(Inventory inventory) {
        int top = switch (kind) {
            case FILE_CABINET -> 78;
            case BRIEFCASE -> 71;
            default -> 114;
        };
        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(inventory, column + row * 9 + 9, 8 + column * 18, top + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(inventory, column, 8 + column * 18, top + 58));
        }
    }

    DeviceBlock.Kind kind() {
        return kind;
    }

    int queueLength() { return printerData.getCount() == 0 ? 0 : printerData.get(0); }
    int printerState() { return printerData.getCount() == 0 ? 0 : printerData.get(1); }
    int currentPage() { return printerData.getCount() == 0 ? 0 : printerData.get(2); }
    int totalPages() { return printerData.getCount() == 0 ? 0 : printerData.get(3); }
    int pageProgress() { return printerData.getCount() == 0 ? 0 : printerData.get(4); }
    int blocker() { return printerData.getCount() == 0 ? 0 : printerData.get(5); }

    @Override
    public boolean stillValid(Player player) {
        return player.distanceToSqr(device.devicePosition().getX() + 0.5,
                device.devicePosition().getY() + 0.5, device.devicePosition().getZ() + 0.5) <= 64;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = slots.get(index);
        if (!slot.hasItem()) return ItemStack.EMPTY;
        ItemStack original = slot.getItem();
        ItemStack copy = original.copy();
        if (index < machineSlotCount) {
            if (!moveItemStackTo(original, machineSlotCount, slots.size(), true)) return ItemStack.EMPTY;
        } else if (!moveItemStackTo(original, 0, machineSlotCount, false)) {
            return ItemStack.EMPTY;
        }
        if (original.isEmpty()) slot.set(ItemStack.EMPTY);
        else slot.setChanged();
        return copy;
    }
}
