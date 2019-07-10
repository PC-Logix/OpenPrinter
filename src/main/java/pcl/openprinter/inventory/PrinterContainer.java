package pcl.openprinter.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Slot;
import pcl.openprinter.inventory.slots.*;
import pcl.openprinter.tileentity.PrinterTE;

/**
 * @author Caitlyn
 *
 */
public class PrinterContainer extends CustomContainer {
    protected PrinterTE tileEntity;


    public PrinterContainer (InventoryPlayer inventoryPlayer, PrinterTE te){
            tileEntity = te;
            //Black Ink
            addSlotToContainer(new PrinterInkBlackSlot(tileEntity, 0, 30, 47));
            //Color Ink
            addSlotToContainer(new PrinterInkColorSlot(tileEntity, 1, 60, 47));
            //Blank Paper
            addSlotToContainer(new PrinterPaperSlot(tileEntity, 2, 129, 47));

            //Output slots
            for (int i = 3; i < 12; i++) {
            	addSlotToContainer(new PrinterOutputSlot(tileEntity, i, 8 + i * 18 - 54, 87));
            }
            
            addSlotToContainer(new ScannerSlot(tileEntity, 13, 94, 17));
            
            //commonly used vanilla code that adds the player's inventory
            bindPlayerInventory(inventoryPlayer);
    }

    protected void bindPlayerInventory(InventoryPlayer inventoryPlayer) {
        for (int i = 0; i < 3; i++) {
            for (int j = 0; j < 9; j++) {
                addSlotToContainer(new Slot(inventoryPlayer, j + i * 9 + 9, 8 + j * 18, 84 + i * 18 + 15 + 15));
            }
        }

        for (int i = 0; i < 9; i++) {
            addSlotToContainer(new Slot(inventoryPlayer, i, 8 + i * 18, 142 + 15 + 15));
        }
    }

}
