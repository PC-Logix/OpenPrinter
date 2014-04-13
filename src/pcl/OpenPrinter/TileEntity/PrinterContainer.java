package pcl.openprinter.tileentity;

import pcl.openprinter.gui.PrinterInkBlackSlot;
import pcl.openprinter.gui.PrinterInkColorSlot;
import pcl.openprinter.gui.PrinterPaperSlot;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
/**
 * @author Caitlyn
 *
 */
public class PrinterContainer extends Container{
    protected PrinterTE tileEntity;

    public PrinterContainer (InventoryPlayer inventoryPlayer, PrinterTE te){
            tileEntity = te;
            //Color Ink
            addSlotToContainer(new PrinterInkBlackSlot(tileEntity, 0, 30, 17));
            //Black Ink
            addSlotToContainer(new PrinterInkColorSlot(tileEntity, 1, 60, 17));
            //Blank Paper
            addSlotToContainer(new PrinterPaperSlot(tileEntity, 2, 129, 17));

            //Output slots
            for (int i = 3; i < 12; i++) {
            	addSlotToContainer(new Slot(tileEntity, i, 8 + i * 18 - 54, 57));
            }
            
            //commonly used vanilla code that adds the player's inventory
            bindPlayerInventory(inventoryPlayer);
    }

    @Override
    public boolean canInteractWith(EntityPlayer player) {
            return tileEntity.isUseableByPlayer(player);
    }


    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
            for (int i = 0; i < 3; i++) {
                    for (int j = 0; j < 9; j++) {
                            addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18));
                    }
            }

            for (int i = 0; i < 9; i++) {
                    addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142));
            }
    }

    @Override
    public ItemStack transferStackInSlot(EntityPlayer player, int slot) {
            ItemStack stack = null;
            Slot slotObject = (Slot) inventorySlots.get(slot);

            //null checks and checks if the item can be stacked (maxStackSize > 1)
            if (slotObject != null && slotObject.getHasStack()) {
                    ItemStack stackInSlot = slotObject.getStack();
                    stack = stackInSlot.copy();

                    //merges the item into player inventory since its in the tileEntity
                    if (slot < 9) {
                            if (!this.mergeItemStack(stackInSlot, 0, 35, true)) {
                                    return null;
                            }
                    }
                    //places it into the tileEntity is possible since its in the player inventory
                    else if (!this.mergeItemStack(stackInSlot, 0, 9, false)) {
                            return null;
                    }

                    if (stackInSlot.stackSize == 0) {
                            slotObject.putStack(null);
                    } else {
                            slotObject.onSlotChanged();
                    }

                    if (stackInSlot.stackSize == stack.stackSize) {
                            return null;
                    }
                    slotObject.onPickupFromSlot(player, stackInSlot);
            }
            return stack;
    }

}
