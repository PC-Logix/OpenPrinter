package pcl.openprinter.client;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.item.Item;
import pcl.openprinter.ContentRegistry;
import pcl.openprinter.OpenPrinter;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CreativeTab extends CreativeTabs {
	public CreativeTab(String unlocalizedName) {
		super(unlocalizedName);
	}

	@Override
	@SideOnly(Side.CLIENT)
	public Item getTabIconItem() {
		return Item.getItemFromBlock(ContentRegistry.printerBlock);
	}
}