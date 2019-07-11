package pcl.openprinter.inventory;

import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.items.ItemStackHandler;
import pcl.openprinter.items.PrintedPage;

import javax.annotation.Nonnull;

public class FolderInventory extends ItemStackHandler {
    public ItemStack folderStack;

    public FolderInventory(ItemStack stack){
        super(9);

        folderStack = stack;

        if(!folderStack.hasTagCompound())
            folderStack.setTagCompound(new NBTTagCompound());

        if(!folderStack.getTagCompound().hasKey("inventory"))
            folderStack.getTagCompound().setTag("inventory", serializeNBT());

        deserializeNBT(folderStack.getTagCompound().getCompoundTag("inventory"));
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack){
        return stack.getItem() instanceof PrintedPage;
    }

    @Override
    public void onContentsChanged(int slot){
        folderStack.getTagCompound().setTag("inventory", serializeNBT());
    }
}
