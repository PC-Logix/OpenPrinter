package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import net.minecraft.item.Item;

public class ItemPaperShreds extends Item {
	public ItemPaperShreds() {
		super();
		setMaxStackSize(64);
		setCreativeTab(OpenPrinter.CreativeTab);
		setTranslationKey("openprinter.paper_shreds");
	}

}
