package pcl.openprinter.inventory;

import net.minecraft.item.ItemStack;
import pcl.openprinter.items.ItemBriefcase;

public class BriefcaseItemInventory extends BriefcaseInventory {
    public ItemStack folderStack;

    public BriefcaseItemInventory(ItemStack stack, int slotCount){
        super(slotCount);

        folderStack = stack;

        deserializeNBT(ItemBriefcase.getInventoryFromStack(folderStack));
    }

    @Override
    public void onContentsChanged(int slot){
        ItemBriefcase.saveInventoryToStack(folderStack, this);
    }
}
