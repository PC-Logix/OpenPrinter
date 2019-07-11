package pcl.openprinter.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.items.ItemHandlerHelper;

import java.util.ArrayList;
import java.util.List;

public class CustomContainer extends Container {
    @Override
    public boolean canInteractWith(EntityPlayer player) {
        return true;
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer, int x, int y) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, x + j * 18, y + i * 18));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, x + i * 18, y + 58));
        }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot){
        Slot sourceSlot = getSlot(slot);

        for(Slot targetSlot : getInventorySlots(sourceSlot.inventory, true))
            if(transferToSlot(sourceSlot, targetSlot) == 0) break;

        return ItemStack.EMPTY; //this has to return empty itemstack, otherwise it runs as loop
    }

    protected List<Slot> getInventorySlots(IInventory inventory, boolean invertRule){
        ArrayList<Slot> slots = new ArrayList<>();
        for(Slot slot : inventorySlots) {
            if (invertRule && slot.inventory.getClass().equals(inventory.getClass()))
                continue;

            if (!invertRule && !slot.inventory.equals(inventory))
                continue;

            slots.add(slot);
        }

        return slots;
    }

    protected int transferToSlot(Slot source, Slot target){
        if(source.getStack().isEmpty())
            return 0;

        if(!target.isItemValid(source.getStack()))
            return source.getStack().getCount();

        //return if stack in slot is not equal to input

        ItemStack stack = source.getStack().copy();
        ItemStack stackOld = source.getStack().copy();

        int transfer = Math.min(target.getSlotStackLimit(), source.getStack().getCount());

        if(target.getHasStack()) {
            if (!ItemHandlerHelper.canItemStacksStack(source.getStack(), target.getStack()))
                return source.getStack().getCount();

            transfer = Math.min(transfer, Math.min(target.getStack().getMaxStackSize(), target.getSlotStackLimit()) - target.getStack().getCount());
            stackOld.shrink(transfer);
            stack = target.getStack();
            stack.grow(transfer);
        }
        else{
            stack.setCount(transfer);
            stackOld.shrink(transfer);
        }
        if(transfer > 0) {
            source.putStack(stackOld);
            target.putStack(stack);
        }

        return source.getStack().getCount();
    }

}
