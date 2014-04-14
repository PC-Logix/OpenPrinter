/**
 * 
 */
package pcl.openprinter.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 * 
 */
public class PaperGUI extends GuiScreen {
	
	public static ItemStack stack = null;
	public PaperGUI(World world, EntityPlayer player) { }
	public void initGui(){}
	public void drawScreen(int i, int j, float f){
	drawDefaultBackground();
	int offset = 100;
	drawCenteredString(fontRenderer, stack.stackTagCompound.getString("pageTitle"), width / 2, height / 2 - 110, 0x000000);
	for (int x = 0; x <= stack.stackTagCompound.getTags().size(); x++) {
		drawCenteredString(fontRenderer, stack.stackTagCompound.getString("line"+x), width / 2, height / 2 - offset, stack.stackTagCompound.getInteger("color"+x));
		offset = offset - 10;
	}
	super.drawScreen(i, j, f);
	}
}