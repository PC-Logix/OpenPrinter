package pcl.openprinter.inventory;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import javax.annotation.Nullable;

public class BriefcaseContainer extends CustomContainer {
    public ItemStack briefcaseItemstack;
    public IItemHandler inventory;

    public BriefcaseContainer(EntityPlayer player, InventoryPlayer inventoryPlayer, @Nullable TileEntity tileEntity){
        if(tileEntity == null) {
            briefcaseItemstack = player.getHeldItemMainhand();
            inventory = new BriefcaseItemInventory(briefcaseItemstack, 18);
        }
        else {
            inventory = tileEntity.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
        }


        for (int i = 0, row = 0; i < inventory.getSlots(); row++)
            for (int column = 0; i < inventory.getSlots() && column < 9; i++, column++)
                addSlotToContainer(new SlotItemHandler(inventory, i, 8 + column * 18, 17 + row * 18));


        bindPlayerInventory(inventoryPlayer, 8, 71);
    }

}