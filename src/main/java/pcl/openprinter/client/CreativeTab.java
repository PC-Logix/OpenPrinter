package pcl.openprinter.client;

import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import pcl.openprinter.ContentRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs {
	public CreativeTab(String unlocalizedName) {
		super(unlocalizedName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public ItemStack createIcon() {
		return new ItemStack(Item.getItemFromBlock(ContentRegistry.printerBlock));
	}
}