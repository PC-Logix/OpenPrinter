/**
 * 
 */
package pcl.openprinter.items;

import pcl.openprinter.OpenPrinter;
import pcl.openprinter.blocks.Printer;
import pcl.openprinter.gui.PaperGUI;
import cpw.mods.fml.common.registry.GameRegistry;
import cpw.mods.fml.common.registry.LanguageRegistry;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 *
 */
public class PrintedPage extends Item{

	public PrintedPage() {
		super();
		maxStackSize = 1;
		setCreativeTab(CreativeTabs.tabMaterials);
		setUnlocalizedName("printedPage");
		setTextureName("minecraft:paper");
	}
	
	public ItemStack onItemRightClick(ItemStack par1ItemStack, World par2World, EntityPlayer player)
    {
       player.openGui(OpenPrinter.instance, 1, player.worldObj, (int) player.posX, (int) player.posY, (int) player.posZ);
       PaperGUI.stack = par1ItemStack;
       return par1ItemStack;
    }
}