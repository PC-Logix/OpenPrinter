/**
 * 
 */
package pcl.openprinter.gui;

import org.lwjgl.opengl.GL11;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

/**
 * @author Caitlyn
 * 
 */
public class PaperGUI extends GuiScreen {

	public static String limit(String value, int length)
	{
		StringBuilder buf = new StringBuilder(value);
		if (buf.length() > length)
		{
			buf.setLength(length);
			buf.append("...");
		}

		return buf.toString();
	}

	public static ItemStack stack = null;
	public PaperGUI(World world, EntityPlayer player) { }
	public void initGui(){}
	public void drawScreen(int i, int j, float f){
		drawDefaultBackground();
		final int xSizeOfTexture = 171;
		final int ySizeOfTexture = 208;
		final ResourceLocation theTexture = new ResourceLocation("openprinter", "textures/gui/printerPaper.png");
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(theTexture);

		int posX = (this.width - xSizeOfTexture) / 2;
		int posY = (this.height - ySizeOfTexture) / 2;
		drawTexturedModalRect(posX, posY, 0, 0, xSizeOfTexture, ySizeOfTexture);
		int offset = 100;
		if (stack.hasTagCompound()) {
			if (stack.stackTagCompound.getDouble("version") == 2.0) {
				drawCenteredString(mc.fontRenderer, stack.stackTagCompound.getString("pageTitle"), width / 2, height / 2 - 110, 0x000000);
				for (int x = 0; x <= stack.stackTagCompound.func_150296_c().size(); x++) {
					String output = stack.stackTagCompound.getString("line"+x);
					String[] parts = output.split("∞");
					if(parts.length > 1) {
						Integer outleng = parts[0].replaceAll("(?:§[0-9a-fk-or])+", "").length();
						if (outleng > 30) {
							parts[0] = limit(parts[0],30);
						}
						Integer color = Integer.parseInt(parts[1]);
						String alignment = parts[2];
						if (alignment.equalsIgnoreCase("center")) {
							mc.fontRenderer.drawString(parts[0], width/2 - mc.fontRenderer.getStringWidth(parts[0])/2, height / 2 - offset, color);	
						} else {
							mc.fontRenderer.drawString(parts[0] , width/2 - xSizeOfTexture/2 + 6, height / 2 - offset, color);
						}
						offset = offset - 10;	
					}
				}
			} else {
				drawCenteredString(mc.fontRenderer, stack.stackTagCompound.getString("pageTitle"), width / 2, height / 2 - 110, 0x000000);
				for (int x = 0; x <= stack.stackTagCompound.func_150296_c().size(); x++) {
					String output = stack.stackTagCompound.getString("line"+x);
					Integer outleng = output.replaceAll("(?:§[0-9a-fk-or])+", "").length();
					if (outleng > 30) {
						output = limit(output,30);
					}
					Integer color = stack.stackTagCompound.getInteger("color"+x);
					String alignment = stack.stackTagCompound.getString("alignment"+x);
					if (alignment.equalsIgnoreCase("center")) {
						mc.fontRenderer.drawString(output, width/2 - mc.fontRenderer.getStringWidth(output)/2, height / 2 - offset, color);	
					} else {
						mc.fontRenderer.drawString(output , width/2 - xSizeOfTexture/2 + 6, height / 2 - offset, color);
					}
					offset = offset - 10;
				}				
			}
		} else {
			mc.fontRenderer.drawString("This page intentionally left blank.", width/2 - mc.fontRenderer.getStringWidth("This page intentionally left blank.")/2, height / 2 - offset, 0x000000);	
		}

		super.drawScreen(i, j, f);
	}
}