package pcl.openprinter;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

public interface InventoryDevice extends MenuProvider {
    ItemStackHandler inventory();

    default IItemHandler itemHandler(@Nullable net.minecraft.core.Direction side) {
        return inventory();
    }

    BlockPos devicePosition();

    DeviceBlock.Kind deviceKind();

    default int machineSlots() {
        return inventory().getSlots();
    }

    @Override
    default Component getDisplayName() {
        return Component.translatable("container.openprinter.device");
    }

    @Nullable
    @Override
    default AbstractContainerMenu createMenu(int id, Inventory playerInventory, Player player) {
        return new DeviceMenu(id, playerInventory, this);
    }

    default void dropContents(Level level, BlockPos pos) {
        ItemStackHandler handler = inventory();
        for (int slot = 0; slot < handler.getSlots(); slot++) {
            ItemStack stack = handler.getStackInSlot(slot);
            if (!stack.isEmpty()) {
                level.addFreshEntity(new ItemEntity(level, pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5, stack.copy()));
                handler.setStackInSlot(slot, ItemStack.EMPTY);
            }
        }
    }
}
