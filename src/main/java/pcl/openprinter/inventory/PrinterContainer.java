package pcl.openprinter.inventory;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import pcl.openprinter.tileentity.PrinterTE;

public class PrinterContainer extends CustomContainer {
    protected PrinterTE tileEntity;


    public PrinterContainer (InventoryPlayer inventoryPlayer, PrinterTE te){
            tileEntity = te;
            IItemHandler inventoryMaterials = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.NORTH);

            //Black Ink
            addSlotToContainer(new SlotItemHandler(inventoryMaterials, 0, 30, 47));
            //Color Ink
            addSlotToContainer(new SlotItemHandler(inventoryMaterials, 1, 60, 47));
            //Blank Paper
            addSlotToContainer(new SlotItemHandler(inventoryMaterials, 2, 129, 47));


            IItemHandler inventoryOutput = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.DOWN);

            //Output slots
            for (int i = 0; i < inventoryOutput.getSlots(); i++) {
            	addSlotToContainer(new SlotItemHandler(inventoryOutput, i, 8 + i * 18, 87));
            }

            IItemHandler inventoryScanner = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, EnumFacing.UP);
            addSlotToContainer(new SlotItemHandler(inventoryScanner, 0, 94, 17));
            
            //commonly used vanilla code that adds the player's inventory
            bindPlayerInventory(inventoryPlayer, 8, 114);
    }

}
